import { useEffect, useState } from "react";
import { Transition } from "@headlessui/react";
import {
  getLastSwaps,
  getProvider,
  getSubscription,
  getReserves,
  getAmountOut,
  formatUnits,
} from "../ethereumFunctions";
import WrongNetwork from "../Components/WrongNetwork";
import { useWeb3React } from "@web3-react/core";
import { Toaster } from "react-hot-toast";
import { BigNumber } from "ethers/lib";
import { LineChart, Line, CartesianGrid, XAxis, YAxis, Tooltip } from 'recharts';
import { ConfigField } from "./CoinField";

function PoolTracker(props) {
  const { account, chainId } = useWeb3React();
  const [wrongNetworkOpen, setwrongNetworkOpen] = useState(false);
  const [lastEvents, setLastEvents] = useState([]);
  const [eventsTimestamp, setEventsTimestamp] = useState(new Date());
  const [reservesTimestamp, setReservesTimestamp] = useState(new Date());
  const [amountTimestamp, setAmountTimestamp] = useState(new Date());
  const [reserves, setReserves] = useState([BigNumber.from(0), BigNumber.from(0), '0']);
  const [amountOut, setAmountOut] = useState(['0', '0']);
  const [showTransition, setShowTransition] = useState(false);
  const [subscriptionActive, setSubscriptionActive] = useState(false);
  const [poolRatio, setPoolRatio] = useState(['0', '0']);
  
  const [chartData, setChartData] = useState([]);
  const [scale, setScale] = useState([0, 0]);
  const [gradient, setGradient] = useState(1);

  useEffect(() => {
    setShowTransition(true);
  }, []);

  useEffect(() => {
    // if (account && chainId === 5001) {
    if (account && chainId === 137) {
      props.setupConnection();
      setwrongNetworkOpen(false);
    } else {
      setwrongNetworkOpen(true);
    }
  }, [account, chainId]);

  useEffect(() => {
    function setupPage() {
      if ((!props.network.signer || props.network.signer.current === null) ||
        (!props.network.factory || props.network.factory.current === null)) {
        return setTimeout(setupPage, 1000);
      }
      if (subscriptionActive || pageSet) {
        return;
      }
      pageSet = true;
      const address1 = '0x7ceB23fD6bC0adD59E62ac25578270cFf1b9f619';
      const address2 = '0x0d500B1d8E8eF31E21C99d1Db9A6444d3ADf1270';
      fetchData(address1, address2);
      setSubscription(address1, address2);
    }
    let pageSet = false;
    setupPage();
  }, [props.network.account, props.network.factory, props.network.router, props.network.signer, wrongNetworkOpen]);

  useEffect(estimateRange, [scale, gradient, reserves]);

  function fetchData(address1, address2) {
    fetchReserves(address1, address2);
    fetchAmounts(address1, address2);
    fetchSwaps(address1, address2);
  }

  async function setSubscription(address1, address2) {
    const filter = await getSubscription(address1,
      address2,
      props.network.factory,
      props.network.signer
    );
    getProvider().on(filter, () => {
      fetchData(address1, address2);
    });
    setSubscriptionActive(true);
  }

  async function fetchReserves(address1, address2) {
    const reserves = await getReserves(address1, address2, props.network.factory,
        props.network.signer, props.network.account);
    if (!reserves || !reserves.length || !reserves[0] || reserves[0].isZero()) {
      return;
    }
    const poolRatio = [];
    const precision = 18;
    poolRatio.push(reserves[0].mul('1' + '0'.repeat(precision)).div(reserves[1]));
    poolRatio.push(reserves[1].mul('1' + '0'.repeat(precision)).div(reserves[0]));
    setReservesTimestamp(new Date());
    setReserves(reserves);
    setPoolRatio([formatUnits(poolRatio[0], precision), formatUnits(poolRatio[1], precision)]);
  }

  async function fetchAmounts(address1, address2) {
    const amount1 = await getAmountOut(address1, address2, BigNumber.from('1' + '0'.repeat(18)),
        props.network.factory, props.network.router, props.network.signer);
    const amount2 = await getAmountOut(address2, address1, BigNumber.from('1' + '0'.repeat(18)),
        props.network.factory, props.network.router, props.network.signer);
    setAmountTimestamp(new Date());
    setAmountOut([formatUnits(amount1, 18), formatUnits(amount2, 18)]);
  }

  async function fetchSwaps(address1, address2) {
    const swapsData = await getLastSwaps(address1, address2, props.network.factory, props.network.signer);
    const swaps = await Promise.all(swapsData.slice(-3).map(async swap => {
      const normalDirection = swap.args.amount1In.isZero();
      const timestamp = new Date((await swap.getBlock()).timestamp * 1000);
      return {
        sender: swap.args.sender,
        to: swap.args.to,
        normalDirection,
        timestamp,
        amountIn: formatUnits(swap.args['amount' + +!normalDirection + 'In'], 18),
        amountOut: formatUnits(swap.args['amount' + +normalDirection + 'Out'])
      };
    }));
    setEventsTimestamp(new Date());
    setLastEvents(swaps.reverse());
  }

  function estimateRange() {
    function estimateAmountOut(x, y, range) {
      const result = [];
      result.push(formatUnits(x.add(x.mul(range).div(100)), 18));
      result.push(formatUnits(y.sub(y.mul(range).div(100)), 18));
      return result;
    }
  
    if (scale.length !== 2 || (scale[0] === scale[1]) ||
      isNaN(scale[0]) || scale[0] === '' ||
      isNaN(scale[1]) || scale[1] === '' ||
      isNaN(gradient) || gradient === '') {
      return;
    }
    const data = [];
    if (!reserves[0] || reserves[0] === '0') {
      return;
    }
    let signedGradient = gradient;
    if (scale[0] > scale[1]) {
      signedGradient *= -1;
    }
    console.log(scale, signedGradient);
    for (let range = +scale[0]; range <= +scale[1]; range += signedGradient) {
      console.log(range);
      const el = {};
      const amount = estimateAmountOut(reserves[0], reserves[1], range);
      el.gx = amount[0];
      el.gy = amount[1];
      el.range = range;
      data.push(el);
    }
    console.log(data);
    setChartData(data);
  }

  const handleChange = {
    scale0: (e) => {
      setScale([e.target.value, scale[1]]);
    },
    scale1: (e) => {
      setScale([scale[0], e.target.value]);
    },
    gradient: (e) => {
      setGradient(e.target.value);
    },
  };

  return (
    <div className="flex justify-center min-h-screen sm:px-16 px-6">
      <div className="flex justify-between items-center flex-col max-w-[1280px] w-full">
        <div className="flex-1 flex justify-start items-center flex-col w-full mt-2">
          <div className="mt-10 w-full flex justify-center">
            <div className="relative md:max-w-[700px] md:min-w-[500px] min-w-full max-w-full p-[2px] rounded-3xl">
              <div className="w-full bg-primary-gray backdrop-blur-[4px] rounded-3xl shadow-card flex flex-col p-10">
                {wrongNetworkOpen ? (
                  <WrongNetwork></WrongNetwork>
                ) : (
                  <Transition
                    appear={true}
                    show={showTransition}
                    enter="transition ease-out duration-500"
                    enterFrom="opacity-0 translate-y-2"
                    enterTo="opacity-100 translate-y-0"
                    leave="transition ease-in duration-500"
                    leaveFrom="opacity-100 translate-y-0"
                    leaveTo="opacity-0 translate-y-1"
                  >
                    <div className="font-semibold text-white">
                      <div className="text-xl">Feed for Polygon Quickswap, ETH/MATIC pool.</div>
                      <div className="flex justify-between">
                        <div className="text-lg m-3 flex-1">Amount out.</div>
                        <div className="flex-1 m-3 text-right">Last update: {amountTimestamp.toTimeString().split(' ')[0]}</div>
                      </div>
                      1 WETH = {amountOut[0]} WMATIC<br/>
                      1 WMATIC = {amountOut[1]} WETH 
                    </div>
                    <div className="font-semibold text-white">
                      <div className="flex justify-between">
                        <div className="text-lg m-3 flex-1">Reserves.</div>
                        <div className="flex-1 m-3 text-right">Last update: {reservesTimestamp.toTimeString().split(' ')[0]}</div>
                      </div>
                      WETH: {formatUnits(reserves[0], 18)}<br/>
                      WMATIC: {formatUnits(reserves[1], 18)}
                    </div>
                    <div className="font-semibold text-white">
                      <div className="flex justify-between">
                        <div className="text-lg m-3 flex-1">Pool Ratio.</div>
                      </div>
                      1 WETH = {poolRatio[1]} WMATIC<br/>
                      1 WMATIC = {poolRatio[0]} WETH
                    </div>
                    <div className="font-semibold text-white">
                      <div className="flex justify-between">
                        <div className="text-lg m-3 flex-1">Swaps.</div>
                        <div className="flex-1 m-3 text-right">Last update: {eventsTimestamp.toTimeString().split(' ')[0]}</div>
                      </div>
                      {lastEvents.map((swap, index) => {
                        return (<div className="border m-1 p-2 border-r" key={index}>
                          <div className="flex justify-between">
                            <div className="flex-1">WETH {!swap.normalDirection ? '→' : '←'} WMATIC</div>
                            <div>{swap.timestamp.toTimeString().split(' ')[0]}</div>
                          </div>
                          Sender: {swap.sender}<br/>
                          Recipient: {swap.to}<br/>
                          Amount in: {swap.amountIn + ' ' + (!swap.normalDirection ? 'WETH' : 'WMATIC')}<br/>
                          Amount out: {swap.amountOut + ' ' + (swap.normalDirection ? 'WETH' : 'WMATIC')}
                        </div>)
                      })}
                    </div>

                    <div className="mb-6 w-[100%]">
                      <ConfigField
                        activeField={true}
                        value={scale[0]}
                        onChange={handleChange.scale0}
                        fieldName="Scale Min"
                      />
                      <ConfigField
                        activeField={true}
                        value={scale[1]}
                        onChange={handleChange.scale1}
                        fieldName="Scale Max"
                      />
                    </div>

                    <div className="mb-6 w-[100%]">
                      <ConfigField
                        activeField={true}
                        value={gradient}
                        onChange={handleChange.gradient}
                        fieldName="Gradient"
                      />
                    </div>
                    <LineChart margin={{left: 50}} width={400} height={400} data={chartData}>
                      <Line dot={false} type="linear" dataKey="gx" stroke="#8884d8" />
                      <CartesianGrid stroke="#ccc" strokeDasharray="5 5" />
                      <XAxis type="number" dataKey="gy" />
                      <YAxis unit=" WETH" />
                      <Tooltip />
                    </LineChart>
                    <LineChart margin={{left: 50}} width={400} height={400} data={chartData}>
                      <Line type="linear" dot={false} dataKey="gy" stroke="#8884d8" />
                      <CartesianGrid stroke="#ccc" strokeDasharray="5 5" />
                      <XAxis type="number" dataKey="gx" />
                      <YAxis
                        // domain={[0, 'dataMax + ' + (chartData[0].gy - 1000000)]}
                        unit=" WMATIC" tickFormatter={(value) =>
                        new Intl.NumberFormat("en-US", {
                          notation: "compact",
                          compactDisplay: "short",
                        }).format(value)} allowDataOverflow={true} type="number"/>
                      <Tooltip />
                    </LineChart>
                  </Transition>)}
              </div>
            </div>
          </div>
        </div>
      </div>
      <Toaster
        position="bottom-right"
        reverseOrder={false}
        gutter={8}
        containerClassName=""
        containerStyle={{}}
        toastOptions={{
          // Define default options
          className: "border border-primary-green",
          duration: 5000,
          style: {
            background: "#15171A",
            color: "#65B3AD",
          },
        }}
      />
    </div>
  );
}

export default PoolTracker;

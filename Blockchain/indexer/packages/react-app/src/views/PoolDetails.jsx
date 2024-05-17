import { useContractLoader } from "eth-hooks";
import { React, useCallback, useEffect, useState } from "react";
import { useHistory, useParams } from "react-router-dom";
import Button from "../components/Button";
import Label from "../components/Label";
import Price from "../components/Price";
import Modal from "../components/Modal";
import PriceChange from "../components/PriceChange";
import LoadingSpinner from "../components/LoadingSpinner";
import { IMAGES_PATH, INDEXER_API, INDEXER_IMAGES } from "../constants";
import { Transactor } from "../helpers";
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';
import strings from "../translation/translation";
import { ShortText } from "../components";
  
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

export default function PoolDetails({address, provider, contractConfig, chainId, signer, mainnetProvider, gasPrice, isDarkMode}) {
  const [poolData, setPoolData] = useState();
  const [loading, setLoading] = useState();
  const [chartData, setChartData] = useState([]);
  const [userData, setUserData] = useState();
  const localContracts = useContractLoader(provider, contractConfig, chainId);
  // localContract && setContract(localContract);
  const tx = Transactor(provider, gasPrice);
  let [showModal, setShowModal] = useState(false);
  let [showWithdrawModal, setShowWithdrawModal] = useState(false);
  const params = useParams();
  const ticker = params.id;
  let addressSet = false;
  const [ref, setRef] = useState();
  const [legend, setLegend] = useState();

  const onRefChange = useCallback((node) => {
    setRef(node);
    if (node !== null) {
      console.log(node);
      // setLegend(node.getContext());
    }
  });

  useEffect(() => {
    setLoading(true);
    fetch(INDEXER_API + "fund/" + ticker)
      .then(res => res.json())
      .then((poolData) => {
        setPoolData(poolData);
        // address = "0x782620ad951B457711bCE87fE2588a0b57fCf2E1";
        if (!address) {
          return fetchRegularChart();
        }
        return fetchUserData().then(res => res.json())
          .then((data) => {
            if (data && !data.code) {
              setUserData(data);
              const requestOptions = {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ fundTicker: ticker, chartType: "month", userAddress: address })
              };
              return fetch(INDEXER_API + "fund/user/chart", requestOptions);
            } else {
              return fetchRegularChart();
            }        
          })
      })
      .then((res) => res.json())
      .then((data) => {
        setChartData(data);
        setLoading(false);
      })
      .catch((error) => {
        console.log(error);
        setLoading(false);
      });
  }, []);

  useEffect(() => {
    if (!localContracts || Object.keys(localContracts).length === 0 || !poolData || addressSet) {
      return;
    }
    const pool = poolData;
    addressSet = true;
    pool.assets.forEach(asset => {
      asset.address = asset.ticker === "BTC" || asset.ticker === "ETH" ? localContracts["Wrapped" + asset.name].address :
                                                                                    localContracts[asset.name].address;
    });
    setPoolData(pool);
  }, [localContracts, poolData]);

  async function fetchRegularChart() {
    const requestOptions = {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ fundTicker: ticker, chartType: "month" })
    };
    return fetch(INDEXER_API + "fund/chart", requestOptions);
  }

  async function fetchUserData() {
    const requestOptions = {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ fundTicker: ticker, userAddress: address })
    };
    return fetch(INDEXER_API + "fund/user", requestOptions)
  }

  function changeTimePeriod(event) {
    console.log(event.target.value);
  }

  return (
    <div className="default-padding" style={{marginTop: "40px", textAlign: "left"}}>
      {loading ?
        (<LoadingSpinner></LoadingSpinner>) :
        poolData && 
        (<>
          <div className="mobile-column">
            <div>
              <div className="title" style={{display: "flex", alignItems: "center"}}>
                <img src={IMAGES_PATH + "/fund-logo.svg"} style={{width: "100px", height: "100px", marginRight: "20px"}}></img>
                <div className="name" style={{flex: 1}}>{ poolData.name }</div>
              </div>
              <div className="wrapped-flex" style={{marginTop: "60px"}}>
                <div>
                  <Label text={strings.currentPrice} extraStyle={{flex: 1}}></Label>
                  <div style={{flex: 1}}>
                    <Price big={true} number={poolData.tokenInfo.price}></Price>
                  </div>
                </div>
                <div>
                  <Label text="Change" extraStyle={{flex: 1}}></Label>
                  <div style={{flex: 1}}>
                    <PriceChange big={true} number={poolData.tokenInfo.change}></PriceChange>
                  </div>
                </div>
              </div>
            </div>  
            <div className="buttons" style={{display: "flex", flexDirection: "column", alignSelf: "flex-start"}}>
              {/* <Button extraStyle={{marginBottom: "10px"}} buttonClass="deposit" click={() => setShowModal(true)} title="Deposit"></Button>
              <Button buttonClass="withdraw" click={() => setShowWithdrawModal(true)} title="Withdraw"></Button> */}
              <Modal 
                contractConfig={contractConfig}
                address={address}
                provider={provider}
                chainId={chainId}
                signer={signer}
                assets={poolData.assets}
                mainnetProvider={mainnetProvider}
                setIsOpen={setShowWithdrawModal}
                mode="withdraw"
                fundTokenAddress={poolData.tokenInfo.address}
              />
            </div>
          </div>
          {userData && (
            <>
              <h2>{strings.yourInvestment}</h2>
              <div className="wrapped-flex">
                <div>
                  <Label text={strings.tokens} extraStyle={{flex: 1}}></Label>
                  <div style={{flex: 1}}>
                    <ShortText big={true} text={userData.balance.toLocaleString("en-US", {maximumFractionDigits: 4}) + " " + ticker}/>
                  </div>
                </div>
                <div>
                  <Label text={strings.value} extraStyle={{flex: 1}}></Label>
                  <div style={{flex: 1}}>
                  <ShortText big={true} text={userData.totalValue.toLocaleString("en-US", {maximumFractionDigits: 2}) + " USD"}/>
                  </div>
                </div>
              </div>
            </>
          )}
          <h2>{strings.historicalPerformance}</h2>
          <Line
            ref={onRefChange}
            datasetIdKey="id"
            data={{
              labels: chartData.map(el => (new Date(el.time).toLocaleString())),
              datasets: [
                {
                  id: 0,
                  label: ticker,
                  data: chartData.map(el => el.changeFund * 100),
                  pointRadius: 1,
                  backgroundColor: !isDarkMode ? "#5781F1" : "#2379F9",
                  borderColor: !isDarkMode ? "#5781F1" : "#2379F9",
                },
                {
                  id: 1,
                  label: 'BTC',
                  data: chartData.map(el => (el.changeBtc * 100)),
                  pointRadius: 1,
                  backgroundColor: '#f7931a',
                  borderColor: '#f7931a',
                },
                {
                  id: 2,
                  label: 'ETH',
                  pointRadius: 1,
                  data: chartData.map(el => el.changeEth * 100),
                  backgroundColor: !isDarkMode ? "#7E7E7E" : "#7E7E7E",
                  borderColor: !isDarkMode ? "#7E7E7E" : "#7E7E7E",
                }
              ]
            }}
            options={{
              scales: {
                y: {
                  ticks: {
                    callback: function(label) {
                      return label + "%";
                    }
                  }
                },
                x: {
                  ticks: {
                    callback: function(label, context) {
                      let currentDate = (new Date(chartData[context].time));
                      currentDate = currentDate.getDay() + 1 + " " + currentDate.toLocaleString('default', { month: 'short' });
                      if (context === 0 ||
                          (new Date(chartData[context].time).toLocaleDateString()) !==
                          (new Date(chartData[context - 1].time).toLocaleDateString())) {
                        return currentDate;
                      } else {
                        return null;
                      }
                    }
                  }
                }
              },
              plugins: {
                legend: {
                  generateLegend: () => "Pizza",
                  position: "bottom",
                  align: "start"
                },
                tooltip: {
                  callbacks: {
                    label: function(context) {
                      let label = context.dataset.label || '';

                      if (label) {
                          label += ': ';
                      }
                      if (context.parsed.y !== null) {
                          label += context.parsed.y.toFixed(2) + "%";
                      }
                      return label;
                  }
                  }
                }
              }
            }}
          />
          {legend}
          <div className="period">
            <div>{strings.period}</div>
            <div onChange={changeTimePeriod}>
              <input type="radio" value="MALE" name="gender"/> {strings.oneDay}
              <input type="radio" value="MALE" name="gender"/> {strings.oneWeek}
              <input type="radio" value="MALE" name="gender"/> {strings.oneMonth}
            </div>
          </div>
          <h2>{strings.about}</h2>
          <div className="description" style={{flex: 1, marginTop: "10px"}}>{ poolData.descLong }</div>
          <div className="wrapped-flex" style={{marginTop: "25px"}}>
            <div>
              <Label text={strings.marketCap} extraStyle={{flex: 1}}></Label>
              <ShortText
                big={true}
                extraStyle={{flex: 1}}
                text={
                  "$" +
                  (poolData.tokenInfo.supply * poolData.tokenInfo.price)
                    .toLocaleString("en-US", {maximumFractionDigits: 2})
                  }
                />
            </div>
            <div>
              <Label text={strings.currentSupply} extraStyle={{flex: 1}}></Label>
              <ShortText
                big={true}
                extraStyle={{flex: 1}}
                text={
                  (+poolData.tokenInfo.supply).toLocaleString("en-US", {maximumFractionDigits: 4})
                  }
                />
            </div>
          </div>
          <h2>{strings.tokens}</h2>
          <div className="tokenList wrapped-flex" style={{flex: 1}}>
                    {poolData.assets.map(coin => {
                      return (
                      <div
                        className="coin"
                        style={{fontSize: "12px", display: "flex", marginRight: "2px", borderRadius: "2px", flexDirection: "column", alignItems: "center", margin: "3px 10px 0 0"}}
                        key={coin.ticker}
                      >
                        <img style={{height: "40px", margin: "6px 37px"}} src={INDEXER_IMAGES + 'assets/' + coin.ticker.toLowerCase() + ".svg"}></img>
                        <div style={{marginRight: 0, fontSize: "14px", fontWeight: 600}}>{coin.ticker}</div>
                      </div>);
                    })}
            </div>
        </>)
      }
      {showModal &&
        <Modal 
          contractConfig={contractConfig}
          address={address}
          provider={provider}
          chainId={chainId}
          signer={signer}
          assets={poolData.assets}
          mainnetProvider={mainnetProvider}
          setIsOpen={setShowModal}
          mode="deposit"
        />
      }
      {showWithdrawModal &&
        <Modal 
          contractConfig={contractConfig}
          address={address}
          provider={provider}
          chainId={chainId}
          signer={signer}
          assets={poolData.assets}
          mainnetProvider={mainnetProvider}
          setIsOpen={setShowWithdrawModal}
          mode="withdraw"
          fundTokenAddress={poolData.tokenInfo.address}
        />
      }
    </div>
  );
}

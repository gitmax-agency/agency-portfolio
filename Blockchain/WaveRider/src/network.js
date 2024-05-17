import { useRef } from "react";
import { ethers } from "ethers";
import {
  getAccount,
  getFactory,
  getRouter,
  getOracle,
  getNetwork,
  getWeth,
} from "./ethereumFunctions";
import COINS from "./constants/coins";
import * as chains from "./constants/chains";
import { InjectedConnector } from "@web3-react/injected-connector";

const { ethereum } = window;

export const switchNetwork = async () => {
  console.log("switchNetwork")
  try {
    await ethereum.request({
      method: "wallet_switchEthereumChain",
      params: [{ chainId: "0x89" }],
      // params: [{ chainId: "0x13881" }],
    });
  } catch (switchError) {
    // This error code indicates that the chain has not been added to MetaMask.
    if (switchError.code === 4902) {
      try {
        await ethereum.request({
          method: "wallet_addEthereumChain",
          params: [
            // {
            //   chainId: "0x1389",
            //   chainName: "Mantle Testnet",
            //   nativeCurrency: {
            //     name: "BIT Token",
            //     symbol: "BIT",
            //     decimals: 18,
            //   },
            //   rpcUrls: ["https://rpc.testnet.mantle.xyz"],
            //   blockExplorerUrls: ["https://explorer.testnet.mantle.xyz"],
            // },
            {
              chainId: "0x89",
              chainName: "Polygon",
              nativeCurrency: {
                name: "Polygon Token",
                symbol: "MATIC",
                decimals: 18,
              },
              // rpcUrls: ["https://polygon-mumbai-bor.publicnode.com"],
              rpcUrls: ["https://polygon-rpc.com"],
              blockExplorerUrls: ["https://polygonscan.com/"],
            },
          ],
        });
      } catch (addError) {
        console.error(addError);
      }
    }
    console.log(switchError);
  }
};

export const formatAddress = (value, length = 4) => {
  return `${value.substring(0, length + 2)}...${value.substring(
    value.length - length
  )}`;
};

export const injected = new InjectedConnector({
  // supportedChainIds: [5001, 1, 5],
  supportedChainIds: [80001, 1, 5, 137],
});

const Web3ProviderCore = (props) => {
  let network = Object.create({});
  network.provider = useRef(null);
  network.signer = useRef(null);
  network.account = useRef(null);
  network.coins = [];
  network.chainID = useRef(null);
  network.router = useRef(null);
  network.oracle = useRef(null);
  network.factory = useRef(null);
  network.weth = useRef(null);
  async function setupConnection() {
    // console.log("Web3ProviderCore::setupConnection")
    try {
      network.provider = new ethers.providers.Web3Provider(window.ethereum);
      network.signer = await network.provider.getSigner();
      await getAccount().then(async (result) => {
        network.account = result;
      });

      await getNetwork(network.provider).then(async (chainId) => {
        // Set chainID
        network.chainID = chainId;
        if (chains.networks.includes(chainId)) {
          // Get the router using the chainID
          network.router = await getRouter(
            chains.routerAddress.get(chainId),
            network.signer
          );
          // console.log("network.router", network.router);
          // Get default coins for network
          network.coins = COINS.get(chainId);
          // console.log("network.coins", network.coins);
          // Get Weth address from router
          await network.router.WETH().then((wethAddress) => {
            network.weth = getWeth(wethAddress, network.signer);
            // Set the value of the weth address in the default coins array
            network.coins[0].address = wethAddress;
          });
          // console.log("network.weth", network.weth);
          // Get the factory address from the router
          await network.router.factory().then((factory_address) => {
            network.factory = getFactory(factory_address, network.signer);
          });
          // console.log("network.factory", network.factory);
          // await network.factory.oracle().then((oracle_address) => {
          //   network.oracle = getOracle(oracle_address, network.signer);
          // });
        } else {
          console.log("Wrong network mate.");
        }
      });
    } catch (e) {
      console.log(e);
    }
  }
  return <>{props.render(network, setupConnection)}</>;
};

export default Web3ProviderCore;

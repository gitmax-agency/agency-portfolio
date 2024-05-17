// import { StatusBar } from 'expo-status-bar';
import React from 'react';
import { StyleSheet } from 'react-native';
import {
  BrowserRouter as Router,
  Route,
  Routes
} from "react-router-dom";
import { APP_PATH } from './src/config';
import { Escrow } from './src/views/Escrow';
import { Metamask } from './src/views/Metamask';

// import { Connectors } from 'web3-react';
// import 'web3';
// const { InjectedConnector, NetworkOnlyConnector } = Connectors;
 
// const MetaMask = new InjectedConnector({ supportedNetworks: [1, 4] });
 
// const Infura = new NetworkOnlyConnector({
//   providerURL: 'https://mainnet.infura.io/v3/...'
// });
 
// const connectors = { MetaMask };

// const Web3 = require('web3');
// const Contract = require('web3-eth-contract');
// console.log(Web3);
// const web3Instance = new Web3('https://goerli.infura.io/v3/f3fdd2d5fb5e4a0b9bb922648f9647f9');
// console.log(web3Instance);
// Contract.setProvider('https://goerli.infura.io/v3/f3fdd2d5fb5e4a0b9bb922648f9647f9');

// var contract = new Contract('abi', 0x0Fa640be33847E524befe67130Af0144b4f68541);
export default function App() {
  return (
    <div style={{margin: "0 20%"}}>
      <Router>
        <Routes>
          <Route path={APP_PATH + "/"} element={<Metamask/>}/>
          <Route path={APP_PATH + "/contract/:address"} element={<Escrow/>}/>
        </Routes>
      </Router>
    </div>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});

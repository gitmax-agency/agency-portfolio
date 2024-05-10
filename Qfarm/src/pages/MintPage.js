import Navbar from "../components/Navbar";
import {useState, useEffect} from "react";
import { Contract, ethers, BigNumber } from 'ethers';
import ABI from './abi.json';
import { async } from "@firebase/util";
// import MetaMask from '../components/MetaMask';
const NFTAddress = "0x4700a2235e890D4f6924d667198cdC99B22a5C02";
// let provider = new ethers.providers.JsonRpcProvider(`https://polygon-mainnet.g.alchemy.com/v2/NJSHEhUVZgsPopdQcwkVVWcOH58c4uCk`)

export default function MintPage() {
    let [counter, setCounter] = useState(1);
    let [total, setTotal] = useState(150);
    let [available, setAvailable] = useState(48);
    let [nft, setNFT] = useState();
    let [address, setAddress] = useState();
    // let [address, setAddress] = useState('');
    // const connectMetamask = async () => {
    //     const provider = new ethers.providers.Web3Provider(window.ethereum)
    //     const accounts = await provider.send("eth_requestAccounts", []);
    //     const balance = await provider.getBalance(accounts[0]);
    //     setAddress(accounts[0]);
    //     console.log(balance);
    // }

    useEffect(() => {
        const provider = new ethers.providers.Web3Provider(window.ethereum);
        // const signer = provider.getSigner()
        const nftContract = new ethers.Contract(NFTAddress, ABI, provider);
        // const maxBuy = await nftContract.maximumBuyAmount();
        setNFT(nftContract);
        async function setMax() {
            const maxBuy = await nft.maximumBuyAmount();
            const sellCounter = await nft.sellCounter();
            console.log("MMMMM: ", maxBuy);
            console.log("sellCounter: ", sellCounter);
            return {maxBuy, sellCounter};
        }
        setMax().then((maxBuy, sellCounter)=>{
            setTotal(BigNumber.from(maxBuy).toNumber());
            setAvailable(BigNumber.from(sellCounter).toNumber());
            // setCounter(maxBuy-sellCounter);
        });
    }, []);

    
    const connectMetamask = async () => {
        const provider = new ethers.providers.Web3Provider(window.ethereum)
        const accounts = await provider.send("eth_requestAccounts", []);
        const balance = await provider.getBalance(accounts[0]);
        setAddress(accounts[0]);
        console.log(balance);
    }
    const incrementCounter = () => {
        setCounter(++counter)
    }

    const decrementCounter = () => {
        setCounter(--counter)
    }


    const buyNFT = async () => {
        const provider = new ethers.providers.Web3Provider(window.ethereum);
        const signer = provider.getSigner();
        await nft.connect(signer).buyNFT(address);
    }

    const metaMaskRender = () => {
        if(address){
            return (
                <div className="button">
                <button onClick={() => buyNFT()} className="default-button">
                            Buy
                </button>
         </div>
            )
        }
        return(
            <div className="button">
                    <button onClick={() => connectMetamask()} className="default-button">
                                CONNECT WALLET
                    </button>
             </div>
        )
    }

    return (
        <div className="mint-page">
            <Navbar />

            <div className="mint-block">
                <div className="inner">
                    <div className="title">
                        Public Mint is <span style={{color: "#FD4A4A"}}>LIVE</span>
                    </div>

                    <div className="available-container">
                        <p>Available</p>
                        <p>{available}/{total}</p>
                    </div>

                    <div className="amount">
                        <p>Amount</p>
                        <div className={"counter"}>
                            <button onClick={incrementCounter}>+</button>
                            <input style={{width: `${(''+counter).length}ch`}} type="text" value={counter} onChange={(e)=>{setCounter(+e.target.value)}}/>
                            <button onClick={decrementCounter}>-</button>
                        </div>
                        <button>MAX</button>
                    </div>

                    <div className="price">
                        <p>Price</p>
                        <p>{3 * counter}</p>
                    </div>
                    {metaMaskRender()}
                    {/* <MetaMask connectMetamask={connectMetamask} /> */}
                </div>
            </div>
        </div>
    )
}
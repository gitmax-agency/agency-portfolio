import { useContractLoader } from "eth-hooks";
import { React, useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import { Transactor } from "../helpers";
import Button from "./Button";
import Label from "./Label";
import Price from "./Price";
import Modal from "./Modal";
import PriceChange from "./PriceChange";
import LoadingSpinner from "./LoadingSpinner";
import { IMAGES_PATH, INDEXER_API, INDEXER_ASSETS, INDEXER_IMAGES, ROOT_PATH } from "../constants";

export default function Pool({
  ticker,
  name,
  description,
  provider,
  gasPrice
}) {
  
  const [poolData, setPoolData] = useState();
  const [loading, setLoading] = useState();
  // localContract && setContract(localContract);
  const tx = Transactor(provider, gasPrice);

  const history = useHistory(); 
  
  const routeChange = () =>{ 
    let path = ROOT_PATH + "/fund/" + ticker;
    history.push(path);
  }

  useEffect(() => {
    setLoading(true);
    fetch(INDEXER_API + "fund/" + ticker)
      .then(res => res.json())
      .then((poolData) => {
        setPoolData(poolData);
        setLoading(false);
      })
      .catch((error) => {
        console.log(error);
        setLoading(false);
      });
  }, []);

  const poolStyle = {
    display: "flex",
    padding: "15px",
    // alignItems: "center",
    flexDirection: "column",
    // flexGrow: 1,
    borderRadius: "10px",
    textAlign: "left",
    height: "230px"
  }
  
  return (
    <>
      <div className="pool shadow" style={poolStyle} onClick={routeChange}>
        <div className="title" style={{display: "flex", alignItems: "center"}}>
          <img src={IMAGES_PATH + "/logo.png"} style={{width: "40px", height: "40px", marginRight: "10px"}}></img>
          <div className="name" style={{flex: 1}}>{ name }</div>
        </div>
        <div className="description" style={{flex: 1, marginTop: "10px"}}>{ description }</div>
        {loading ?
          (<LoadingSpinner></LoadingSpinner>) :
          poolData && 
          (<>
            <div style={{display: "flex", marginTop: "10px"}}>
              <Label text="Current price" extraStyle={{flex: 1}}></Label>
              <Label text="vs USD 1 month" extraStyle={{flex: 1}}></Label>
            </div>
            <div style={{display: "flex"}}>
              <div style={{flex: 1}}>
                <Price big={true} number={poolData.tokenInfo.price}></Price>
                <PriceChange number={poolData.tokenInfo.change}></PriceChange>
              </div>
              <img src={IMAGES_PATH + "/logo.png"} style={{flex: 1, width: "100%", height: "40px"}}></img>
            </div>
            <div style={{display: "flex", marginTop: "10px"}}>
              <Label text="Total invested" extraStyle={{flex: 1}}></Label>
              <Label text="Tokens" extraStyle={{flex: 1}}></Label>
            </div>
            <div style={{display: "flex"}}>
              <Price extraStyle={{flex: 1}} big={false} number={poolData.tokenInfo.supply * poolData.tokenInfo.price}></Price>
              <div className="tokenList" style={{flex: 1, display: "flex"}}>
                {poolData.assets.map(coin => {
                  return (
                  <div className="coin" style={{fontSize: "12px", marginRight: "2px", borderRadius: "2px"}} key={coin.ticker}>
                    <img style={{height: "14px"}} src={INDEXER_ASSETS + coin.ticker.toLowerCase() + ".svg"}></img>
                  </div>);
                })}
              </div>
            </div>
          </>)
        }
      </div>
    </>
  );
}

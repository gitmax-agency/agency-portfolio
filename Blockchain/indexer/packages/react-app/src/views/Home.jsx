import { useContractReader } from "eth-hooks";
import React, { useEffect, useState } from "react";
import Pool from "../components/Pool";
import TextBlock from "../components/TextBlock";
import { IMAGES_PATH, INDEXER_API } from "../constants";
import strings from "../translation/translation";

function Home({ address, provider, contractConfig, chainId, signer, mainnetProvider }) {
  const [isLoaded, setIsLoaded] = useState(false);
  const defaultPools = [
    {
        "ticker": "LCAP",
        "name": "Large Caps",
        "descShort": "Top coins by capitalization",
        "address": "0x8e38595aE994b1bABB575ade7744BE66ebdEFaAE",
        "networkId": 2,
        "timeAdded": "2022-05-24T16:59:37.000Z",
        "status": 1
    }
  ];
  const [pools, setPools] = useState(defaultPools);
  useEffect(() => {
    fetch(INDEXER_API + "fund/list")
      .then(res => res.json())
      .then(
        (result) => {
          setIsLoaded(true);
          setPools(result);
        },
        (error) => {
          console.log(error);
          setIsLoaded(true);
        }
      )
  }, [])
  const text1 = strings.topText;
  const text2 = strings.bottomText;

  return (
    <div className="default-padding">
      <div style={{marginTop: "40px"}}><h1 style={{fontSize: "60px", marginBottom: "10px"}}>{strings.homeTitle}</h1></div>
      <div style={{width: "100%", padding: "0 20%", fontSize: "20px"}}>{strings.homeSubtitle}</div>
      {pools && !!pools.length &&
      (<div style={{marginTop: "80px"}}>
        <div className="column-3">
          {pools.map(pool => (
            <Pool 
              key={pool.address}
              name={pool.name}
              ticker={pool.ticker}
              description={pool.descShort}
              poolAddress={pool.address}
              address={address}
              provider={provider}
              contractConfig={contractConfig}
              chainId={chainId}
              signer={signer}
              mainnetProvider={mainnetProvider}
            ></Pool>
          ))}
        </div>
      </div>)}
      <TextBlock extraStyle={{marginTop: "80px"}} header={strings.topTitle} text={text1} image={IMAGES_PATH + "/pig.png"} imagePosition="left"></TextBlock>
      <TextBlock extraStyle={{marginTop: "80px"}} header={strings.bottomTitle} text={text2} image={IMAGES_PATH + "/pig.png"} imagePosition="right"></TextBlock>
    </div>
  );
}

export default Home;

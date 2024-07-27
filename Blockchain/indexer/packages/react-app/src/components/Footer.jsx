import React from "react";
import strings from "../translation/translation";

// displays a page header

export default function Footer({web3Modal, loadWeb3Modal, logoutOfWeb3Modal,}) {
  return (
    <div className="dark-background default-padding" style={{position: "absolute", bottom: 0, marginTop: "40px", display: "flex", width: "100%", height: "100px", alignItems: "center", justifyContent: "space-between"}}>
      <div style={{textAlign: "left", fontWeight: "bold", fontSize: "16px"}}>{strings.title}</div>
      <div style={{display: "flex", justifyContent: "space-evenly", width: "500px"}}>
        <div className="home-link">{strings.invest}</div>
        <div className="home-link">{strings.stakeEarn}</div>
        <div className="home-link">{strings.about}</div>
        <div className="home-link">{strings.currentLanguage}</div>
        {web3Modal && !web3Modal.cachedProvider &&
          <div className="home-link" onClick={loadWeb3Modal}>{strings.connectWallet}</div>
        }
        {web3Modal && web3Modal.cachedProvider &&
          <div className="home-link" onClick={logoutOfWeb3Modal}>{strings.disconnectWallet}</div>
        }
      </div>
    </div>
  );
}

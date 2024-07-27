import strings from "../translation/translation";
import React from "react";
import { ThemeSwitch } from ".";
import Button from "./Button";
import { slide as Menu } from "react-burger-menu";
import { useHistory } from "react-router-dom";
import { ROOT_PATH } from "../constants";

// displays a page header

export default function Header({web3Modal, loadWeb3Modal, logoutOfWeb3Modal, isDarkMode, setIsDarkMode}) {
  const history = useHistory(); 
  
  const routeChange = () => {
    let path = ROOT_PATH + "/fund/LCAP";
    history.push(path);
  }
  return (
    // <a href={link} target="_blank" rel="noopener noreferrer">
    //   <img src="logo_transparent.png" style={{height: "100px", width: "100px", position: "absolute", top: "6px", left: "14px"}}></img>
    //   <PageHeader
    //     title={title}
    //     subTitle={subTitle}
    //     style={{ cursor: "pointer", visibility: "hidden" }}
    //   />
    // </a>
    <div className="default-background default-padding" style={{position: "fixed", top: 0, display: "flex", width: "100%", height: "60px", alignItems: "center", justifyContent: "space-between"}}>
      <div onClick={routeChange} style={{textAlign: "left", fontWeight: "bold", fontSize: "16px", cursor: "pointer"}}>{strings.title}</div>
      <div className="header-links" style={{display: "flex", justifyContent: "space-evenly", width: "320px"}}>
        <div className="home-link">{strings.invest}</div>
        <div className="home-link">{strings.stakeEarn}</div>
        <div className="home-link">{strings.about}</div>
        <div className="home-link">{strings.currentLanguage}</div>
        <div className="home-link" style={{display: "flex", alignItems: "center"}}>
          <ThemeSwitch isDarkMode={isDarkMode} setIsDarkMode={setIsDarkMode} />
        </div>
      </div>
      <div style={{display: "flex", justifyContent: "flex-end"}}>
        <Button extraStyle={{marginRight: "8px"}} buttonClass="header-button" title="Polygon"  />
        {web3Modal && !web3Modal.cachedProvider &&
          <Button click={loadWeb3Modal} buttonClass="header-button" title={strings.connectWallet}  />
        }
        {web3Modal && web3Modal.cachedProvider &&
          <Button click={logoutOfWeb3Modal} buttonClass="header-button" title={strings.disconnectWallet}  />
        }
      </div>
      {/* <Menu right>
        <a id="home" className="menu-item" href="/">Home</a>
        <a id="about" className="menu-item" href="/about">About</a>
        <a id="contact" className="menu-item" href="/contact">Contact</a>
      </Menu> */}
    </div>
  );
}


Header.defaultProps = {
  link: "",
  title: "Indexer",
  subTitle: "",
}
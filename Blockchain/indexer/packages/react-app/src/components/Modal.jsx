import React, { useCallback, useEffect, useState } from "react";
import styles from "./Modal.module.css";
import { RiCloseLine } from "react-icons/ri";
import Balance from "./Balance";
import { useContractLoader } from "eth-hooks";
import { Transactor } from "../helpers";
import { utils, BigNumber } from "ethers";
import Button from "./Button";
import strings from "../translation/translation";
import Label from "./Label";
import { IMAGES_PATH, INDEXER_ASSETS, INDEXER_IMAGES } from "../constants";
import Select from 'react-select'

const Modal = ({ setIsOpen, address, provider, contractConfig, gasPrice, fundTokenAddress, signer, assets, chainId }) => {
    const [amount, setAmount] = useState(0);
    const [balance, setBalance] = useState();
    const [refreshRequired, triggerRefresh] = useState(false);
    const [currentAsset, setCurrentAsset] = useState();
    const [mode, setMode] = useState("deposit");
    const [assetsList, setAssetsList] = useState(assets);
    let [erc20Contract, setErc20] = useState();
    let fundToken;
    const localContracts = useContractLoader(provider, contractConfig, chainId);
    const localContract = localContracts ? localContracts['Fund'] : null;
    const tx = Transactor(provider, gasPrice);

    const config = Object.assign({}, contractConfig);
    config["customAddresses"] = {FundToken: fundTokenAddress};
    const importedContracts = useContractLoader(provider, config, 0);
    fundToken = importedContracts ? importedContracts['FundToken'] : null;

    const selectStyles = {
        control: (styles) => ({
                    ...styles,
                    // backgroundColor: "blue",
                    borderRadius: "10px",
                }),
        indicatorSeparator: (styles) => ({
            display: "none"
        })
    }
    

    // getAssets();

    // async function getAssets() {

        // const poolId = await tx(localContract ? localContract[getContractFunction(localContract, "poolId")]() : null);
        // const assetsRequest = vaultContract ? vaultContract[getContractFunction(vaultContract, "getPoolTokens")] : null;
        // const assetsList = assetsRequest && await tx(assetsRequest(poolId));
    const refresh = useCallback(async () => {
        try {
            getBalance();
            triggerRefresh(false);
        } catch (e) {
            console.log(e);
        }
    }, [fundToken, triggerRefresh, mode]);

    useEffect(() => {
        refresh();
      }, [refresh, refreshRequired, localContract, mode]);

    async function depositFunds() {
        const approval = await approve();
        if (!approval) {
            return;
        }
        const funcName = localContract ? getContractFunction(localContract, "depositAsset") : null;
        const contractFunc = localContract.connect(signer)[funcName];
        const returned = await tx(contractFunc(currentAsset, convertToBigNumber(amount)));
        const receipt = returned ? await returned.wait() : "Error";

        // const check = await tx(erc20Contract.connect(signer)[getContractFunction(erc20Contract, "allowance")](address, localContract.address));

        getBalance();
    }

    async function withdrawFunds() {
        const funcName = localContract ? getContractFunction(localContract, "withdrawAsset") : null;
        const contractFunc = localContract.connect(signer)[funcName];
        const returned = await tx(contractFunc(currentAsset, convertToBigNumber(amount)));
        const receipt = returned ? await returned.wait() : "Error";

        getBalance();
    }
    async function approve() {
        const contractFunc = erc20Contract.connect(signer)[getContractFunction(erc20Contract, "approve")];
        return tx(contractFunc(localContract.address, convertToBigNumber(amount)));
        // const receipt = returned ? await returned.wait() : "Error";
    }

    function convertToBigNumber(number) {
        const decimals = 18;
        // number = parseFloat(number);
        return utils.parseEther(number);
    }

    function convertFromBigNumber(number) {
        // number = BigNumber.from(number).toString();
        // return "" + number / (10 ** 18);
        return utils.formatEther(number);
    }

    function chooseAsset(asset) {
        setCurrentAsset(asset.value);
        setBalance(asset.balance);
        // const erc20ContractName = localContracts ? 
        //             Object.keys(localContracts).find((contract) => localContracts[contract].address === asset) : null;
        // if (erc20ContractName) {
        //     erc20Contract = localContracts[erc20ContractName];
        //     setErc20(localContracts[erc20ContractName]);
        // }
        // mode === "deposit" && getBalance();
    }

    async function getBalance() {
        if (mode === "deposit") {
            for (let i = 0; i < assets.length; i++) {
                let erc20Contract;
                const erc20ContractName = localContracts ? 
                            Object.keys(localContracts).find((contract) => localContracts[contract].address === assets[i]) : null;
                if (erc20ContractName) {
                    erc20Contract = localContracts[erc20ContractName];
                    
                    const returned = await tx(erc20Contract[getContractFunction(erc20Contract, "balanceOf")](address))
                    assets[i].balance = returned;
                }
            };
            setAssetsList(assets);
            // if (erc20Contract) {
            //     const returned = await tx(erc20Contract[getContractFunction(erc20Contract, "balanceOf")](address));
            //     setBalance(convertFromBigNumber(returned));
            // }
        } else if (mode === "withdraw") {
                // const returned = await tx(localContract[getContractFunction(localContract, "getFundTokenBalance")](address));
                // setBalance(convertFromBigNumber(returned));
            if (fundToken) {
                const balance = fundToken ? await tx(fundToken[getContractFunction(fundToken, "balanceOf")](address)) : 0;
                setBalance(convertFromBigNumber(balance));
            }
        }
    }

    function getContractFunction(contractData, name) {
        return Object.keys(contractData.interface.functions).find(
          func => contractData.interface.functions[func].name === name,
        );
    }

    return (
        <div className={styles.modal}>
            <div className={styles.modalHeader}>
                <div onClick={() => setMode("deposit")} className={styles.heading + (mode === "withdraw" ? " gray-background" : "")}>{strings.deposit}</div>
                <div onClick={() => setMode("withdraw")} className={styles.heading + (mode === "deposit" ? " gray-background" : "")}>{strings.withdraw}</div>
            </div>
            <div className={styles.modalContent}>
                <div style={{marginTop: "5px"}}>
                    <Label text={strings.wallet}/>
                    <Select
                        isClearable={false}
                        isSearchable={false}
                        options={assetsList.map((asset) => {return {
                            value: asset.address,
                            text: asset.name,
                            icon: <img
                                        style={{height: "20px", marginRight: 5}}
                                        src={INDEXER_ASSETS + asset.ticker.toLowerCase() + ".svg"}
                                />,
                            balance: asset.balance
                        }})}
                        styles={selectStyles}
                        placeholder={strings.selectPlaceholder}
                        getOptionLabel={e => (
                            <div style={{ display: 'flex', alignItems: 'center', justifyContent: "space-between" }}>
                              <span>
                                {e.icon}{e.text}
                              </span>
                              <span style={{textAlign: "left"}}>{(+e.balance).toLocaleString("en-US", {maximumFractionDigits: 5})}</span>
                            </div>
                        )}
                        onChange={chooseAsset}
                    />
                    {/* <select
                        style={{padding: "9px 12px", borderRadius: "10px", width: "100%", height: "40px", backgroundImage: "url(" + INDEXER_IMAGES + "assets/btc.svg" + ")", backgroundRepeat: "no-repeat"}}
                    >
                        <option style={{backgroundImage: "url(" + INDEXER_IMAGES + "assets/btc.svg" + ")"}}>BTC</option>
                    </select> */}
                </div>
                <div style={{marginTop: "15px"}}>
                    <Label text={strings.amount}/>
                    <div style={{position: "relative"}}>
                        <input
                            style={{padding: "9px 12px", borderRadius: "10px", width: "100%", height: "40px", border: "solid 1px hsl(0, 0%, 80%)"}}
                            value={amount} onChange={(e) => setAmount(e.target.value)}
                            type="text"
                        />
                        <Button
                            extraStyle={{position: "absolute", right: "10px", width: "44px", height: "21px", borderRadius: "5px", textTransform: "uppercase", fontSize: "14px", fontWeight: 600, padding: 0, top: "9px"}}
                            title={strings.max}
                            click={() => setAmount(balance)}
                        />
                    </div>
                </div>
                {/* {mode === "deposit" && (<Button click={approve} title="Approve"/>)} */}
            </div>
            <div className={styles.modalActions}>
                <div className={styles.actionsContainer}>
                <Button
                    buttonClass={styles.deleteBtn}
                    title={mode === "deposit" ? strings.approveDeposit : strings.withdraw}
                    click={mode === "deposit" ? depositFunds : withdrawFunds}
                />
                </div>
            </div>
        </div>
    );
};

export default Modal;
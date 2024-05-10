// @ts-ignore
import logo from '../assets/images/logo_white.png'
import Dropdown from "./Dropdown";
import {ChevronRightIcon} from "@heroicons/react/24/outline";
import {useNavigate} from "react-router-dom";

export interface LayoutProps  { 
    metamask?: any;
 }
export default function Navbar(props?: LayoutProps) {
    const navigate = useNavigate()
    const investorButtonContent = (
        <div className="dropdown-button-content">
            <p className="title">Invest in cattle</p>
            <button>
                How it works
                <ChevronRightIcon/>
            </button>
            <button>
                Why Cattle
                <ChevronRightIcon/>
            </button>
        </div>
    )

    const ownerButtonContent = (
        <div className="dropdown-button-content">
            <p className="title">Access more capital</p>
            <button>
                How it works
                <ChevronRightIcon/>
            </button>
            <button>
                Why Tokenize
                <ChevronRightIcon/>
            </button>
        </div>
    )
    const navBtn = () => {
        if(props?.metamask)
            return props?.metamask();
        return (
            <button className="default-button" onClick={()=>{navigate('/mint')}}>
                    GET STARTED
            </button>
        )
    } 
    return (
        <div className="navbar">
            <div className="logo" onClick={()=>{navigate('/')}}>
                <img src={logo} alt="logo"/>
            </div>
            <div className="buttons">
                <Dropdown buttonTitle={"Investors"} content={investorButtonContent}/>
                <Dropdown buttonTitle={"Owners"} content={ownerButtonContent}/>
                <p>Our story</p>
                {navBtn()}
            </div>
        </div>
    )
}
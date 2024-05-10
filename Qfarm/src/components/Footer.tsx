import {LinkIcon} from "@heroicons/react/24/outline";
import {useNavigate} from "react-router-dom";

export default function Footer() {
    const navigate = useNavigate()

    return (
        <div className="footer">
            <div className="inner">
                <div className="links">
                    <div className="title" onClick={()=>{navigate('/')}}>Main page</div>
                </div>
                <div className="links">
                    <div className="title" onClick={()=>{navigate('/')}}>Investors</div>
                    <div className="link" onClick={()=>{navigate('/')}}>How it works?</div>
                    <div className="link" onClick={()=>{navigate('/')}}>Why cattle?</div>
                </div>
                <div className="links">
                    <div className="title" onClick={()=>{navigate('/')}}>Owners</div>
                    <div className="link" onClick={()=>{navigate('/')}}>How it works?</div>
                    <div className="link" onClick={()=>{navigate('/')}}>Why cattle?</div>
                </div>
                <div className="links">
                    <div className="link" onClick={()=>{navigate('/')}}>
                        <LinkIcon/>
                        Instagram
                    </div>
                    <div className="link" onClick={()=>{navigate('/')}}>
                        <LinkIcon/>
                        Facebook
                    </div>
                </div>
            </div>
            <hr/>
            <div className="copyright">
                © 2022 QFarm
            </div>
        </div>
    )
}
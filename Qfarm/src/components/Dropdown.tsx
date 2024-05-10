import {ChevronDownIcon} from "@heroicons/react/24/outline";
import {useState} from "react";

interface DropdownProps {
    buttonTitle: string
    content: any
}

export default function Dropdown({buttonTitle, content}: DropdownProps) {
    const [show, hide] = useState(false)

    const toggle = () => {
        hide(!show)
    }

    return (
        <div className="dropdown-container">
            <div className="dropdown-button" onClick={toggle}>
                {buttonTitle}
                <ChevronDownIcon className={`${show ? 'rotate-180' : 'rotate-0'}`}/>
            </div>
            <div className={`dropdown-content ${show ? 'show' : 'hide'}`}>
                {content}
            </div>
        </div>
    )
}
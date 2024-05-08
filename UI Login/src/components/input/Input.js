import './Input.css'
const assets = require('../../assets/assets.js');
function Input(props){

    return (
        <span>
            <img src={assets[props.imagePath]} alt="" className='icon'/>
        <input type="text" placeholder={props.placeholder}>
            
        </input>
        </span>
    )
}

export default Input;
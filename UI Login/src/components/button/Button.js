import './Button.css'
function Button(props) {
    return (
        <button className="btn" style={{
            backgroundColor: props.backgroundColor
          }}> {props.title}</button>
    )

}

export default Button;
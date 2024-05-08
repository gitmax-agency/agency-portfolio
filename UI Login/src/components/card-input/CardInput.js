import './CardInput.css'

function CardInput(props){

    return(

        <input type={props.type} placeholder={props.placeholder} style={{
            width: props.width,
            marginLeft:props.left,
            marginRight:props.right,
          }}>
            
        </input>
    )

}

export default CardInput;
import Button from '../components/button/Button';
import Input from '../components/input/Input';
import './Reset.css';
function Reset(){
    return(
        <section className="form">
            <img src={require('../assets/images/Forgot password illustration.png')} alt="Login-illustration" className="image-container"/>
            <h1 className="form__title">Forgot Password</h1>
            <Input placeholder='example@email.com' imagePath={'contactIcon'}/>
            <Button title="RESET" props='#1A4F8B'/>
            <p className="social_title_reset">Already have an account? <a href="/login">Login here</a></p>
        </section>

    )
}

export default Reset;
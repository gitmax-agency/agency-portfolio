import Button from '../components/button/Button';
import Input from '../components/input/Input';
import './Login.css';
function Login (props){
    
    return(
        <section className="form">
            <img src={require('../assets/images/Login-illustration.png')} alt="Login-illustration" className="image-container"/>
            <h1 className="form__title">Welcome Back!</h1>
            <Input placeholder='example@email.com' imagePath={"contactIcon"}/>
            <Input placeholder='password' imagePath={"lockIcon"}/>
            <a href='/reset' className="link">Forgot Password?</a>
            <Button title="LOG IN" props='#1A4F8B'/>
            <p className="social_title">Or connect with social</p>
            <div className="social_button">
                <button className="facebook">Facebook</button>
                <button className="google">Google</button>
            </div>
        </section>
    )
}

export default Login;
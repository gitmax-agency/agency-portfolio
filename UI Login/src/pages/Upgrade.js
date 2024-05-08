import './Upgrade.css'
import CardInput from '../components/card-input/CardInput.js';
import Button from '../components/button/Button.js';
function Upgrade() {
    return (
        <section className="form">
            <h1 className="form__title">Get Pro Account</h1>
            <img src={require('../assets/images/undraw_online_payments_luau 1.png')} alt="Upgrade-illustration" className="image-container" />
            <CardInput placeholder='Card Number' type="text" />
            <div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <CardInput placeholder='MM/YY' width="165px" right="7px" />
                    <CardInput placeholder='CVV' width="165px" type="number" left=" 7px" />
                </div>
                <p className="question">What’s this?</p>
                <CardInput placeholder='ZIP/Postal Code' />
            </div>
            <Button title="UPGRADE" backgroundColor="#BF1541" />
        </section>
    )
}

export default Upgrade;
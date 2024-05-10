import Navbar from "../components/Navbar";
// @ts-ignore
import hero_section_image from '../assets/images/herosection_image.png'
// @ts-ignore
import hero_section_title from '../assets/images/herosection_title.svg'
// @ts-ignore
import line1 from '../assets/images/line1.svg'
// @ts-ignore
import line0 from '../assets/images/line0.svg'
// @ts-ignore
import token from '../assets/images/token.png'
// @ts-ignore
import figure1 from '../assets/images/figure1.svg'
// @ts-ignore
import pie_chart from '../assets/images/Pie Chart Round.png'
// @ts-ignore
import cube from '../assets/images/Round Cube2.png'
// @ts-ignore
import sphere from '../assets/images/Sphere.png'
// @ts-ignore
import chevronRight from '../assets/images/chevronRight.svg'
// @ts-ignore
import Planet from '../assets/images/Planet.png'
// @ts-ignore
import horses from '../assets/images/horses.jpg'

import {ArrowRightIcon} from "@heroicons/react/24/outline";
import Footer from "../components/Footer";
import {useNavigate} from "react-router-dom";

export default function MainPage() {
    const navigate = useNavigate()

    return (
        <div className="main-page">
            <Navbar/>
            <div className="hero-section">
                <div className="info">
                    <div className="title">
                        INVEST SAFELY <br/>
                        <span className="green">
                            INVEST IN QFARM
                        </span>
                    </div>
                    <div className="description">
                        <b>Fortify</b> your wealth through animal husbandry investing. Start investing in hand-selected and professionally managed KZ for as little as $100
                    </div>
                </div>
                <div className="img">
                    <img src={hero_section_image}/>
                </div>
            </div>
            <img style={{marginBottom: "-5px", marginTop: "200px"}} src={line0} alt="" width={"100%"}/>
            <div className="block-1">
                <img src={line1} className={"line"}/>
                <div className="info">
                    <div className="text" style={{zIndex: 10}}>
                        <div className="title">
                            Qfarm - blockchain platform that allows you to invest in animal husbandry
                        </div>
                        <div className="description">
                            <span style={{color: "#2DC653"}}>Build financial freedom for the long term.</span> Investing in animal husbandry can give you cash flow, strong wealth stability, and diversification. We help you get there.
                        </div>
                        <button className="default-button" onClick={()=>{navigate("/mint")}}>
                            GET STARTED NOW
                        </button>
                    </div>
                    <div className="img">
                        <img src={token} alt=""/>
                    </div>
                </div>
            </div>
            <div className="block-2">
                <p className="title">
                    A revolutionary platform for investors and sponsors
                </p>
                <div className="secondary">
                    QFarm unlocks the potential of real estate for individual investors and real estate sponsors
                </div>

                <div className="cards">
                    <div className="card">
                        <svg width="66" height="66" viewBox="0 0 66 66" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <circle cx="33" cy="33" r="32" stroke="white" stroke-width="2"/>
                            <g clip-path="url(#clip0_351_12)">
                                <path d="M16.5 18.3334H49.5" stroke="white" stroke-width="2.75" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M18.3333 18.3334V36.6667C18.3333 37.6392 18.7196 38.5718 19.4072 39.2594C20.0948 39.9471 21.0275 40.3334 21.9999 40.3334H43.9999C44.9724 40.3334 45.905 39.9471 46.5926 39.2594C47.2803 38.5718 47.6666 37.6392 47.6666 36.6667V18.3334" stroke="white" stroke-width="2.75" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M33 40.3334V47.6667" stroke="white" stroke-width="2.75" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M27.5 47.6666H38.5" stroke="white" stroke-width="2.75" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M25.6667 33L31.1667 27.5L34.8334 31.1666L40.3334 25.6666" stroke="white" stroke-width="2.75" stroke-linecap="round" stroke-linejoin="round"/>
                            </g>
                            <defs>
                                <clipPath id="clip0_351_12">
                                    <rect width="44" height="44" fill="white" transform="translate(11 11)"/>
                                </clipPath>
                            </defs>
                        </svg>
                        <div className="title">Investor Marketplace</div>
                        <div className="desc">Fractional ownership in high-quality, US real estate. Create your account and start building your wealth.</div>
                        <button onClick={()=>{navigate("/mint")}}>
                            Invest now
                        </button>

                        <div className={"link-button"}>
                            Learn more about the platform
                            <ArrowRightIcon/>
                        </div>
                    </div>
                    <div className="card">
                        <svg width="66" height="66" viewBox="0 0 66 66" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <circle cx="33" cy="33" r="32" stroke="white" stroke-width="2"/>
                            <g clip-path="url(#clip0_353_13)">
                                <path d="M55.5267 24.8373C57.2682 19.9106 53.6551 14.144 53.5008 13.903C53.349 13.6693 53.0664 13.566 52.7941 13.6486C52.5288 13.7399 52.3565 13.9957 52.3745 14.2713C52.6142 17.7933 52.0333 20.3227 50.6619 21.7947C49.7359 22.783 48.4662 23.28 46.8851 23.28C45.8307 23.28 45.0158 23.0463 45.0072 23.0463C40.8202 21.3214 36.7297 21.064 34.66 21.064C33.6287 21.064 33.0103 21.124 33.0103 21.1267C32.9896 21.124 32.3708 21.064 31.3391 21.064C29.2708 21.064 25.1766 21.3214 21.0398 23.0284C21.0316 23.0315 20.2022 23.2802 19.1117 23.2802C17.5329 23.2802 16.2613 22.783 15.3384 21.7949C13.9627 20.3229 13.3863 17.7935 13.6258 14.2715C13.6438 13.9932 13.4697 13.7373 13.2051 13.6488C12.9369 13.5619 12.6474 13.6695 12.4997 13.9032C12.3433 14.144 8.7323 19.9106 10.473 24.8373C11.2073 26.9159 12.7759 28.5151 15.1482 29.6066C12.9972 30.5443 11.7975 31.2614 11.578 31.7407C11.5001 31.9173 11.5063 32.1181 11.5983 32.2856C11.6747 32.4296 13.5164 35.7779 16.5234 35.7779C16.7735 35.7779 17.0311 35.7521 17.2796 35.7042L20.6173 35.1501C21.4412 37.8624 23.5022 44.0374 25.6315 45.6459C25.2731 46.1673 24.8746 46.9728 24.8267 48.024C24.75 49.6825 25.561 51.3993 27.2404 53.1181C28.0455 53.9494 28.5821 54.3815 31.0321 54.3815C31.3682 54.3815 31.7397 54.3762 32.1349 54.3565H33.8569C34.2721 54.3762 34.6471 54.3815 34.9798 54.3815C37.4298 54.3815 37.9614 53.9479 38.7714 53.1181C40.4489 51.3993 41.263 49.6825 41.1853 48.024C41.14 46.9728 40.7392 46.1673 40.38 45.6459C42.5121 44.0374 44.5662 37.8622 45.3926 35.1501L48.717 35.7011C52.1526 36.35 54.3301 32.437 54.4108 32.2843C54.5024 32.1147 54.5061 31.9139 54.4334 31.7407C54.2096 31.2629 53.015 30.5486 50.8633 29.6054C53.2238 28.5182 54.7946 26.9191 55.5267 24.8373ZM49.0384 29.0151C48.7958 29.0977 48.6297 29.3177 48.6219 29.5767C48.6157 29.8312 48.7682 30.0605 49.0053 30.1607C50.6015 30.8076 52.4464 31.6628 53.0961 32.085C52.581 32.8593 51.2512 34.5557 49.4812 34.5557C49.3044 34.5557 49.1266 34.5378 48.9255 34.5032L45.0562 33.8543C44.7611 33.8098 44.4574 33.9914 44.372 34.2856C42.8762 39.3682 40.5163 44.8115 39.3883 44.8115C39.1306 44.8115 38.9029 44.9703 38.816 45.2115C38.7252 45.4494 38.7941 45.7202 38.9883 45.8854C38.9992 45.894 39.9034 46.7056 39.9666 48.0779C40.0231 49.3874 39.3287 50.7973 37.8954 52.2655C37.1825 52.9982 37.0061 53.2449 33.8764 53.1343H32.1116C29.1411 53.2425 28.8237 52.9935 28.1156 52.2655C26.6929 50.8043 25.9983 49.4015 26.0452 48.0965C26.0991 46.7136 26.9872 45.9166 27.0224 45.8854C27.2169 45.7202 27.2857 45.4494 27.1949 45.2115C27.1046 44.9734 26.879 44.8115 26.6232 44.8115C25.4948 44.8115 23.1348 39.3682 21.6383 34.2856C21.5513 33.9914 21.2548 33.8082 20.9546 33.8543L17.0674 34.5049C15.0249 34.8938 13.5012 32.9562 12.9232 32.0999C13.6172 31.6401 15.43 30.7986 17.0014 30.1607C17.2381 30.0648 17.3948 29.8296 17.3848 29.5767C17.3759 29.3177 17.2135 29.0977 16.9692 29.0151C14.1729 28.0719 12.3751 26.5325 11.6333 24.4375C10.7131 21.8379 11.5553 18.849 12.3941 16.8451C12.5445 19.3863 13.2355 21.3255 14.4542 22.6328C15.6013 23.8577 17.2163 24.5064 19.1242 24.5064C20.4075 24.5064 21.3734 24.2128 21.4623 24.1785C25.4567 22.5326 29.3681 22.2869 31.3461 22.2869C32.3046 22.2869 32.8781 22.344 32.9554 22.35C32.9777 22.35 33.1006 22.35 33.1207 22.3473C33.1359 22.3442 33.7094 22.2869 34.6682 22.2869C36.6445 22.2869 40.5561 22.5326 44.6021 24.198C44.6412 24.2128 45.6039 24.5064 46.8909 24.5064C48.7969 24.5064 50.4125 23.8579 51.559 22.6313C52.781 21.3242 53.4697 19.3863 53.6232 16.8451C54.4616 18.849 55.303 21.8381 54.3809 24.4359C53.6322 26.5325 51.8382 28.0719 49.0384 29.0151Z" fill="white"/>
                                <path d="M30.4525 48.6346C31.0508 48.6346 31.535 49.1185 31.535 49.7171C31.535 50.3152 31.0508 50.7996 30.4525 50.7996C29.8552 50.7996 29.37 50.3152 29.37 49.7171C29.37 49.1183 29.8554 48.6346 30.4525 48.6346Z" fill="white"/>
                                <path d="M35.5461 48.6346C36.1455 48.6346 36.6301 49.1185 36.6301 49.7171C36.6301 50.3152 36.1457 50.7996 35.5461 50.7996C34.9488 50.7996 34.4644 50.3152 34.4644 49.7171C34.4644 49.1183 34.9488 48.6346 35.5461 48.6346Z" fill="white"/>
                            </g>
                            <defs>
                                <clipPath id="clip0_353_13">
                                    <rect width="46" height="46" fill="white" transform="translate(10 11)"/>
                                </clipPath>
                            </defs>
                        </svg>
                        <div className="title">Cattle Sponsors</div>
                        <div className="desc">Fractional ownership in high-quality, US real estate. Create your account and start building your wealth.</div>
                        <button onClick={()=>{navigate("/mint")}}>
                            Invest now
                        </button>

                        <div className={"link-button"}>
                            Learn more about raising more capital
                            <ArrowRightIcon/>
                        </div>
                    </div>
                </div>

            </div>
            <svg className={"line"} width="330" height="318" viewBox="0 0 330 318" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M42 104C42 102.895 42.8954 102 44 102C45.1046 102 46 102.895 46 104L46 140C46 141.105 45.1046 142 44 142C42.8954 142 42 141.105 42 140L42 104Z" fill="#47FF4E"/>
                <path d="M46 316C46 317.105 45.1046 318 44 318C42.8954 318 42 317.105 42 316L42 208C42 206.895 42.8954 206 44 206C45.1046 206 46 206.895 46 208L46 316Z" fill="url(#paint0_linear_353_14)"/>
                <g filter="url(#filter0_f_353_14)">
                    <circle cx="44.5" cy="173.5" r="14.5" fill="#00FF0A"/>
                </g>
                <g clip-path="url(#clip0_353_14)">
                    <path d="M40 175L42.6667 177.667L48 172.333" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M43.9999 163C47.1144 165.755 51.1792 167.19 55.3333 167C55.9381 169.057 56.1231 171.215 55.8774 173.346C55.6318 175.476 54.9604 177.535 53.9031 179.401C52.8459 181.266 51.4245 182.901 49.7232 184.206C48.022 185.511 46.0757 186.462 43.9999 187C41.9242 186.462 39.9779 185.511 38.2767 184.206C36.5754 182.901 35.154 181.266 34.0967 179.401C33.0395 177.535 32.3681 175.476 32.1224 173.346C31.8768 171.215 32.0618 169.057 32.6666 167C36.8206 167.19 40.8855 165.755 43.9999 163" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </g>
                <path d="M44.0014 105C43.8348 85.5675 58.2987 51.6851 105.499 51.6851C152.698 51.6851 239.165 51.6851 276.498 51.6851C322.498 51.6851 328 45.9422 328 0.5" stroke="url(#paint1_linear_353_14)" stroke-width="4"/>
                <defs>
                    <filter id="filter0_f_353_14" x="0" y="129" width="89" height="89" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
                        <feFlood flood-opacity="0" result="BackgroundImageFix"/>
                        <feBlend mode="normal" in="SourceGraphic" in2="BackgroundImageFix" result="shape"/>
                        <feGaussianBlur stdDeviation="15" result="effect1_foregroundBlur_353_14"/>
                    </filter>
                    <linearGradient id="paint0_linear_353_14" x1="44" y1="318" x2="44" y2="206" gradientUnits="userSpaceOnUse">
                        <stop stop-color="#313131"/>
                        <stop offset="0.185417" stop-color="#345F38"/>
                        <stop offset="0.9999" stop-color="#44FF4C"/>
                    </linearGradient>
                    <linearGradient id="paint1_linear_353_14" x1="42.497" y1="-37.1667" x2="343.714" y2="23.3928" gradientUnits="userSpaceOnUse">
                        <stop stop-color="#47FF4E"/>
                        <stop offset="0.932292" stop-color="#47FF4E" stop-opacity="0.5"/>
                        <stop offset="1" stop-color="#47FF4E" stop-opacity="0"/>
                    </linearGradient>
                    <clipPath id="clip0_353_14">
                        <rect width="32" height="32" fill="white" transform="translate(28 159)"/>
                    </clipPath>
                </defs>
            </svg>
            <div className="block-3">
                <div className="titles">
                    <div className="title">
                        Benefits
                    </div>
                    <div className="desc">
                        For investors and owners
                    </div>
                </div>
                <div className="cards">
                    <div className="card">
                        <div className="title">
                            TOKENIZATION
                        </div>
                        <div className="desc">
                            Tokenization removes the <b>middle man</b>, making it easier and cheaper for investors to <b>buy/sell.</b>
                        </div>
                    </div>
                    <div className="card">
                        <div className="title">
                            LOW FEE
                        </div>
                        <div className="desc">
                            Investors can <b>trade tokens</b> almost instantly and for a very low fee (similar to stock market trades).
                        </div>
                    </div>
                    <div className="card">
                        <div className="title">
                            CAPITALIZATION
                        </div>
                        <div className="desc">
                            For owners makes it <b>possible</b> to raise capital without <b>financial intermediaries</b>
                        </div>

                        <div className="link-button" style={{margin: "24px 0"}}>
                            Learn more about raising more capital
                            <ArrowRightIcon/>
                        </div>
                    </div>
                </div>
                <img src={figure1} alt="figure" style={{position: "absolute", top: 0, right: "-10vw"}}/>
            </div>
            <div className="block-4">
                <div className="title">
                    HOW IT WORKS?
                </div>
                <div className="cards">
                    <div className="card">
                        <div className="title">INVESTMENT</div>
                        <img src={pie_chart} alt="pie_chart"/>
                        <div className="title2">
                            Invest in QFarm
                        </div>
                        <div className="desc">
                            A professional farmer from our proven list manages your capital and multiplies it
                        </div>
                        <div className="button">
                            Learn more
                            <ArrowRightIcon/>
                        </div>
                    </div>
                    <img src={chevronRight} alt=""/>
                    <div className="card">
                        <div className="title">GROWTH</div>
                        <img src={cube} alt="cube"/>
                        <div className="title2">
                            Wait growth of livestock
                        </div>
                        <div className="desc">
                            A professional farmer from our proven list manages your capital and multiplies it
                        </div>
                        <div className="button">
                            Learn more
                            <ArrowRightIcon/>
                        </div>
                    </div>
                    <img src={chevronRight} alt=""/>
                    <div className="card">
                        <div className="title">INCOME</div>
                        <img src={sphere} alt="sphere"/>
                        <div className="title2">
                            Get profit from investment
                        </div>
                        <div className="desc">
                            100% of the profit is distributed among investors
                        </div>
                        <div className="button">
                            Learn more
                            <ArrowRightIcon/>
                        </div>
                    </div>
                </div>
                <img src={Planet} alt="Planet" style={{position: "absolute", right: "-10vw", bottom: -1100}}/>
            </div>
            <div className="block-5">
                <div className="title">
                    ROADMAP
                </div>

                <div className="phases">
                    <p className="active">
                        PHASE 1
                    </p>

                    <p className="">
                        PHASE 2
                    </p>
                </div>
                <div className="line">
                    <div className="line1"></div>
                    <div className="dot1">
                        <div className="inner-dot"></div>
                    </div>
                    <div className="line2"></div>
                    <div className="dot2">
                        <div className="inner-dot"></div>
                    </div>
                    <div className="line3"></div>
                </div>

                <div className="phases-info">
                    <div className="info">
                        <div className="title active">
                            DEC 2022
                        </div>
                        <div className="desc active">
                            Analyzing Customer Dev.
                            Making a Unique Selling Proposition
                            Creating and checking the hypothesis
                            Launching MVP
                        </div>
                        <div className="card">
                            <p className="title">
                                PHASE RESULTS
                            </p>
                            <p className="desc">
                                Landing page release
                                First token sales
                                Finding product market fit
                            </p>
                        </div>
                    </div>
                    <div className="info">
                        <div className="title">
                            FEB 2023
                        </div>
                        <div className="desc">
                            Launching a platform for token issuance
                            Legal Structuring of the process of registration of farms
                            Providing detailed Farm reports/indicators for investors
                        </div>
                        <div className="card">
                            <p className="title">
                                PHASE RESULTS
                            </p>
                            <p className="desc">
                                Registration of Qfarm Tokens in AIFC jurisdictions
                                Investment platforms with increasing payments flow
                            </p>
                        </div>
                    </div>
                </div>

                <p className="phase-3">
                    PHASE 3
                </p>
                <div className="line2">
                    <div className="line1"></div>
                    <div className="dot1">
                        <div className="inner-dot"></div>
                    </div>
                    <div className="line2"></div>
                </div>
                <div className="phases-info" style={{justifyContent: 'center'}}>
                    <div className="info">
                        <div className="title">
                            JUNE 2023
                        </div>
                        <div className="desc">
                            Raising an investment of $200,000
                            Private offer for loyal investors
                            Making public STO
                        </div>
                        <div className="card">
                            <p className="title">
                                PHASE RESULTS
                            </p>
                            <p className="desc">
                                Scaling product to the Asian market
                            </p>
                        </div>
                    </div>
                </div>

            </div>
            <div className="invest-block">
                <div className="inner">
                    <div className="info">
                        <p className="title">
                            Building relationships between investors and operators
                        </p>
                        <p className="desc">
                            Get started with HoneyBricks to see how we can help grow your wealth.
                        </p>

                        <div className="buttons">
                            <button className="default-button" onClick={()=>{navigate("/mint")}}>Invest Now</button>
                            <button className="default-button" style={{background: "transparent", border: "2px solid #2DC653", color: '#2DC653'}}>Raise Capital</button>
                        </div>
                    </div>
                    <div className="img">
                        <img src={horses}/>
                    </div>
                </div>
            </div>
            <Footer/>
        </div>
    )
}
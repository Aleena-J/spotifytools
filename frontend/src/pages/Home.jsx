import '../css/Home.css';
import Cookies from 'js-cookie';
import { useState, useEffect } from "react"
import gif from '../assets/kendall.gif'

function Home() {
    const [isLoggedIn, setLogIn] = useState(false);
    
    useEffect(() => {
        const login = Cookies.get('userId');
        if(login === "noID" || login == undefined){
            setLogIn(false)
        }else{
            setLogIn(true)
        }
    }, []);

    return (
        <div className="welcome-body">
            <h1 className='welcome-text'>Welcome!</h1>
            <img src={gif} className='kendall-gif'/>
            {isLoggedIn ? <h2 className='loginText'>You are logged in!</h2> :  <h2 className='loginText'>Log in to get started!</h2>}
        </div>
    );
}

export default Home;
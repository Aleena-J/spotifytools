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

    const getSpotifyLogin = () => {
        fetch("http://127.0.0.1:8080/login", {
            credentials: "include"
        })
        .then(res => res.text())
        .then(authUrl => {
            window.location.replace(authUrl);
        });
    };

    return (
        <div className="welcome-body">
            <h1 className='welcome-text'>Welcome!</h1>
            <img src={gif} className='kendall-gif'/>
            {isLoggedIn ? <h2>You are logged in!</h2> : <button className="loginButton" onClick={getSpotifyLogin}>Login</button>}
        </div>
    );
}

export default Home;
import '../css/Login.css';
import Cookies from 'js-cookie';
import { useState, useEffect } from "react"

function Login() {
    const [isLoggedIn, setLogIn] = useState(false);
    const [loading, setLoading] = useState(true);
    

    useEffect(() => {
        if (!loading && isLoggedIn) {
            window.location.replace("/");
        }
    }, [loading, isLoggedIn]);

    useEffect(() => {
        const login = Cookies.get('userId');
        if(login === "noID" || login == undefined){
            setLogIn(false)
            setLoading(false);
        }else{
            setLogIn(true)
            setLoading(false);
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
        <div className="login-body">
            {isLoggedIn ? <></> : 
                <><h1 className='Login'>Log in to use features!</h1>
                <p className='login-info'>Pressing the button below will redirect you to Spotify's authentication site</p>
                <button className="loginButton" onClick={getSpotifyLogin}>Login</button></>
            }
        </div>
    );
}

export default Login;
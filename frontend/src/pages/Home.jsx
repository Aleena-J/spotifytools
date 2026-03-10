import '../css/Home.css';
import Cookies from 'js-cookie';
import { useState, useEffect } from "react"
import gif from '../assets/kendall.gif'

function Home() {
    const [isLoggedIn, setLogIn] = useState(false);
    
    useEffect(() => {
        const login = localStorage.getItem('userId');
        if(login === "noID" || login == undefined){
            setLogIn(false)
        }else{
            setLogIn(true)
        }
    }, []);

    return (
        <div className="welcome-body">
            <br></br>
            <hr></hr>
            <p>3/9/2026 - As I am not currently paying for Spotify Premium, I no longer have access to the Spotify Web API, making this website nonfunctional</p>
            <p>Below is a video demo of some of the website features in action</p>
            <iframe width="560" height="315" src="https://www.youtube.com/embed/Fa05ZBFatwk?si=k8GsRkuHCu-bnXTI" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
            <br></br>
            <hr></hr>
            <h1 className='welcome-text'>Welcome!</h1>
            <img src={gif} className='kendall-gif'/>
            {isLoggedIn ? <h2 className='loginText'>You are logged in!</h2> :  <h2 className='loginText'>Log in to get started!</h2>}
        </div>
    );
}

export default Home;
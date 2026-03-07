import '../css/NavBar.css'
import { Link } from "react-router-dom";
import { useState, useEffect } from "react"
import Cookies from 'js-cookie';


function NavBar() {

    const [isLoggedIn, setLogIn] = useState(false);

     useEffect(() => {
        const login = localStorage.getItem('userId');
        if(login === "noID" || login == undefined){
            setLogIn(false)
        }else{
            setLogIn(true)
        }
    }, []);

    return(
        <nav className="navbar">
            <div className="navbar-brand">
                <Link to="/" className="brand-link">SpotifyTools</Link>
            </div>
            <div className="navbar-links">
                {isLoggedIn ? <Link to="/logout" className="nav-link">Logout</Link> : <Link to="/login" className="nav-link">Login</Link>}
                {isLoggedIn && <Link to="/repeats" className="nav-link">Repeats</Link>}
                {isLoggedIn && <Link to="/skips" className="nav-link">Skips</Link>}
                {isLoggedIn && <Link to="/sort-playlist" className="nav-link">Sort Playlist</Link>}
            </div>
        </nav>
    )
}

export default NavBar
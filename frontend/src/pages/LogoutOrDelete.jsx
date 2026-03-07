import '../css/LogoutOrDelete.css';
import Cookies from 'js-cookie';
import { useState, useEffect } from "react"

function LogoutOrDelete() {
    const [isLoggedIn, setLogIn] = useState(false);
    const [loading, setLoading] = useState(true);
    
    useEffect(() => {
        if (!loading && !isLoggedIn) {
            window.location.replace("/");
        }
    }, [loading, isLoggedIn]);

    useEffect(() => {
        const login = Cookies.get('userId');
        if(login === "noID" || login == undefined){
            setLogIn(false)
            setLoading(false)
        }else{
            setLogIn(true)
            setLoading(false)
        }
    }, []);

    const deleteAccount = async () => {
        const res = await fetch("https://spotifytools.onrender.com/delete-account", {
            method: "DELETE",
            credentials: "include"
        });
        const data = await res.text();
        if(data === "User not found"){
            alert("Error with deletion, user not found")
        }else if(data === "Deletion successful"){
            alert("Deletion successful")
            Cookies.remove('userId');
            setLogIn(false)
            window.location.replace("/");
        }
    };

    const logout = () => {
        Cookies.remove('userId');
        setLogIn(false)
        window.location.replace("/");
    };

    return (
        <div className="logout-body">
            <h1 className='select-text'>Select an option below!</h1>
            <div className="options-grid">
                <div className='option-logout'>
                    <h2>Logout</h2>
                    <p>This will simply log you out of the website</p>
                    <button className='logout-btn' onClick={logout}>Log out</button>
                </div>

                <div className='option-delete'>
                    <h2>Delete Account</h2>
                    <p>This will delete your account and any data about it from the website</p>
                    <button className='delete-btn' onClick={deleteAccount}>Delete</button>
                </div>
            </div>
        </div>
    );
}

export default LogoutOrDelete;
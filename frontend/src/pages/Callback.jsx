import { useEffect } from 'react';

function Callback() {

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const code = params.get("code");

        if (code) {
            fetch("https://spotifytools.onrender.com/callback", {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "text/plain"
                },
                body: code
            })
            .then(() => {
                window.location.replace("/");
            });
        } else {
             window.location.href("/");
        }
    }, []);
    
    return <p>Logging in...</p>;
}

export default Callback;

import '../css/NavBar.css'
import { Link } from "react-router-dom";


function NavBar() {
    return(
        <nav className="navbar">
            <div className="navbar-brand">
                <Link to="/" className="brand-link">SpotifyTools</Link>
            </div>
            <div className="navbar-links">
                <Link to="/" className="nav-link">Home</Link>
                <Link to="/repeats" className="nav-link">Repeats</Link>
                <Link to="/skips" className="nav-link">Skips</Link>
                <Link to="/sort-playlist" className="nav-link">Sort Playlist</Link>
            </div>
        </nav>
    )
}

export default NavBar
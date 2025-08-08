import './App.css'
import Home from './pages/Home';
import PlaylistSort from './pages/PlaylistSort';
import UserRepeats from './pages/UserRepeats';
//import UserSkips from './pages/UserSkips';
import NavBar from './components/NavBar';
import Login from './pages/Login';
import LogoutOrDelete from './pages/LogoutOrDelete';
import Callback from './pages/Callback';
import {Routes, Route} from 'react-router-dom'

function App() {

  return (
    <>
      <NavBar />
        <main>
          <Routes>
            <Route path="/" element={<Home />}/>
            <Route path="/callback" element={<Callback />} />
            <Route path="/repeats" element={<UserRepeats />}/>
            <Route path="/sort-playlist" element={<PlaylistSort />}/>
            <Route path="/login" element={<Login />}/>
            <Route path="/logout" element={<LogoutOrDelete/>}/>
          </Routes>
        </main>
    </>
  )
}

export default App


          //<Route path="/logout" element={<Logout/>}/>
          //<Route path="/skips" element={<UserSkips />}/>
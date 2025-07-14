import './App.css'
import Home from './pages/Home';
//import PlaylistSort from './pages/PlaylistSort';
import UserRepeats from './pages/UserRepeats';
//import UserSkips from './pages/UserSkips';
import NavBar from './components/NavBar';
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
          </Routes>
        </main>
    </>
  )
}

export default App


          //<Route path="/logout" element={<Logout/>}/>
          //<Route path="/repeats" element={<UserRepeats />}/>
          //<Route path="/skips" element={<UserSkips />}/>
          //<Route path="/sort-playlist" element={<PlaylistSort />}/>
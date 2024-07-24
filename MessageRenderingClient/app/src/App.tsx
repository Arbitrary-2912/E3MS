// App.tsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignIn from './SignIn';
import Messages from './Messages';
import { UserProvider } from './UserContext';

function App() {
    return (
        <UserProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<SignIn />} />
                    <Route path="/messages" element={<Messages />} />
                </Routes>
            </Router>
        </UserProvider>
    );
}

export default App;

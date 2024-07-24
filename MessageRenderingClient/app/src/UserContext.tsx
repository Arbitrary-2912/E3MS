import React, { createContext, useContext, useState, ReactNode } from 'react';

interface UserContextProps {
    username: string;
    password: string;
    setUsername: (username: string) => void;
    setPassword: (password: string) => void;
}

const UserContext = createContext<UserContextProps | undefined>(undefined);

export const UserProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');

    return (
        <UserContext.Provider value={{ username, password, setUsername, setPassword }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => {
    const context = useContext(UserContext);
    if (!context) {
        throw new Error('useUser must be used within a UserProvider');
    }
    return context;
};

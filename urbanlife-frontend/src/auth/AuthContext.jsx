import { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const email = localStorage.getItem('email');
    const role = localStorage.getItem('role');
    const userId = localStorage.getItem('userId');
    const communityId = localStorage.getItem('communityId');
    return email ? { email, role, userId, communityId } : null;
  });

  const login = (email, password, role, userId, communityId) => {
    localStorage.setItem('email', email);
    localStorage.setItem('password', password);
    localStorage.setItem('role', role);
    if (userId) localStorage.setItem('userId', userId);
    if (communityId) localStorage.setItem('communityId', communityId);

    setUser({ email, role, userId, communityId });
  };

  const logout = () => {
    localStorage.clear();
    setUser(null);
    window.location.href = '/login';
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);

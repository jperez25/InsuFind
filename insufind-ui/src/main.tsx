import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Link, Route, Routes } from 'react-router-dom'
import SearchPage from './pages/SearchPage'
import ClaimDetailPage from './pages/ClaimDetailPage'
import './styles.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <header className="app-header">
        <Link to="/" data-testid="app-title">InsuFind</Link>
        <span>Claim search for adjusters</span>
      </header>
      <main>
        <Routes>
          <Route path="/" element={<SearchPage />} />
          <Route path="/claims/:id" element={<ClaimDetailPage />} />
        </Routes>
      </main>
    </BrowserRouter>
  </StrictMode>,
)

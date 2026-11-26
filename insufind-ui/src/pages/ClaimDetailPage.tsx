import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { STATUS_LABEL, getClaim } from '../api'
import type { ClaimDetail } from '../api'

export default function ClaimDetailPage() {
  const { id = '' } = useParams()
  const navigate = useNavigate()
  const [claim, setClaim] = useState<ClaimDetail | null>(null)
  const [state, setState] = useState<'loading' | 'ready' | 'missing' | 'error'>('loading')

  useEffect(() => {
    let current = true
    setState('loading')
    getClaim(id)
      .then((data) => {
        if (!current) return
        setClaim(data)
        setState(data ? 'ready' : 'missing')
      })
      .catch(() => current && setState('error'))
    return () => {
      current = false
    }
  }, [id])

  // Going back restores the previous search (it lives in the URL); fall back to the empty search page.
  function back() {
    if (window.history.length > 1) navigate(-1)
    else navigate('/')
  }

  return (
    <section>
      <button data-testid="back-to-search" onClick={back}>← Back to search</button>
      {state === 'loading' && <p data-testid="loading">Loading…</p>}
      {state === 'missing' && <p data-testid="not-found">Claim not found.</p>}
      {state === 'error' && <p role="alert" data-testid="error-message">Could not load the claim.</p>}
      {state === 'ready' && claim && (
        <article data-testid="claim-detail">
          <h1 data-testid="detail-claim-number">{claim.claimNumber}</h1>
          <dl>
            <dt>Status</dt>
            <dd data-testid="detail-status">{STATUS_LABEL[claim.status]}</dd>
            <dt>Policy</dt>
            <dd data-testid="detail-policy">{claim.policyNumber}</dd>
            <dt>Incident date</dt>
            <dd data-testid="detail-date">{claim.incidentDate}</dd>
            <dt>Location</dt>
            <dd data-testid="detail-location">{claim.location}</dd>
            <dt>Description</dt>
            <dd data-testid="detail-description">{claim.description}</dd>
            <dt>Insured driver</dt>
            <dd data-testid="detail-insured-driver">
              {claim.insuredDriver.fullName} ({claim.insuredDriver.licenseNumber})
            </dd>
            <dt>Other drivers</dt>
            <dd>
              {claim.otherDrivers.length === 0 && <span data-testid="detail-no-other-drivers">None</span>}
              <ul>
                {claim.otherDrivers.map((d) => (
                  <li key={d.id} data-testid="detail-other-driver">
                    {d.fullName} ({d.licenseNumber})
                  </li>
                ))}
              </ul>
            </dd>
          </dl>
        </article>
      )}
    </section>
  )
}

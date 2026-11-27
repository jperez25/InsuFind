import { useEffect, useState } from 'react'
import type { ReactNode } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { STATUS_LABEL, TYPE_LABEL, getClaim } from '../api'
import type { ClaimDetail } from '../api'

const dash = (value: string | null) => value || '—'

// One grid cell: label above value. `wide` spans that many of the 5 columns (true = 1).
function Cell({ label, testId, wide, children }: { label: string; testId?: string; wide?: boolean | number; children: ReactNode }) {
  const span = wide === true ? 1 : wide || 1
  return (
    <div className="detail-cell" style={{ gridColumn: `span ${span}` }}>
      <dt>{label}</dt>
      <dd data-testid={testId}>{children}</dd>
    </div>
  )
}

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
      {state === 'missing' && (
        <div className="empty-state" data-testid="not-found">
          <svg className="empty-logo" viewBox="0 0 48 48" width="72" height="72" aria-hidden="true">
            <path d="M24 4 40 10v12c0 10-7 18-16 22C15 40 8 32 8 22V10z" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinejoin="round" />
            <circle cx="22" cy="22" r="6" fill="none" stroke="currentColor" strokeWidth="2.5" />
            <path d="m26.5 26.5 6 6" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" />
          </svg>
          <strong>Oops! Claim does not exist</strong>
          <span>We couldn't find a claim with that ID. It may have been removed or the link is wrong.</span>
        </div>
      )}
      {state === 'error' && <p role="alert" data-testid="error-message">Could not load the claim.</p>}
      {state === 'ready' && claim && (
        <article data-testid="claim-detail" className="detail-card">
          <h1 data-testid="detail-claim-number">{claim.claimNumber}</h1>
          <dl className="detail-grid">
            <Cell label="Status" testId="detail-status">{STATUS_LABEL[claim.status]}</Cell>
            <Cell label="Type" testId="detail-type">{TYPE_LABEL[claim.claimType]}</Cell>
            <Cell label="Incident date" testId="detail-date">{claim.incidentDate}</Cell>
            <Cell label="Reported date" testId="detail-reported-date">{dash(claim.reportedDate)}</Cell>
            <Cell label="Loss state" testId="detail-loss-state">{dash(claim.lossState)}</Cell>

            <Cell label="Policy #" testId="detail-policy">{claim.policyNumber}</Cell>
            <Cell label="Policy state" testId="detail-policy-state">{dash(claim.policyState)}</Cell>
            <Cell label="License plate" testId="detail-plate">{dash(claim.vehiclePlate)}</Cell>
            <Cell label="VIN" testId="detail-vin">{dash(claim.vehicleVin)}</Cell>
            <Cell label="Location" testId="detail-location">{claim.location}</Cell>

            <Cell label="Insured driver" testId="detail-insured-driver" wide>
              {claim.insuredDriver.fullName} ({claim.insuredDriver.licenseNumber})
            </Cell>
            <Cell label="Other drivers" wide={4}>
              {claim.otherDrivers.length === 0 && <span data-testid="detail-no-other-drivers">None</span>}
              <ul>
                {claim.otherDrivers.map((d) => (
                  <li key={d.id} data-testid="detail-other-driver">
                    {d.fullName} ({d.licenseNumber})
                  </li>
                ))}
              </ul>
            </Cell>

            <Cell label="Description" testId="detail-description" wide={5}>{claim.description}</Cell>
          </dl>
        </article>
      )}
    </section>
  )
}

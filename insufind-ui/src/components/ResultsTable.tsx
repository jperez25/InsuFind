import { useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { STATUS_LABEL, TYPE_LABEL } from '../api'
import type { ClaimSummary } from '../api'

export type ColumnKey =
  | 'claim' | 'incidentDate' | 'status' | 'insured' | 'others'
  | 'type' | 'reportedDate' | 'policy' | 'vin' | 'plate' | 'lossState' | 'policyState'

interface Column {
  key: ColumnKey
  label: string
  /** Text shown in the cell; also the value the column sorts by. */
  text: (c: ClaimSummary) => string
}

const dash = (value: string | null | undefined) => value || '—'

export const COLUMNS: Column[] = [
  { key: 'claim', label: 'Claim', text: (c) => c.claimNumber },
  { key: 'incidentDate', label: 'Incident date', text: (c) => c.incidentDate },
  { key: 'status', label: 'Status', text: (c) => STATUS_LABEL[c.status] },
  { key: 'insured', label: 'Insured driver', text: (c) => c.insuredDriver.fullName },
  { key: 'others', label: 'Other drivers', text: (c) => c.otherDrivers.map((d) => d.fullName).join(', ') || '—' },
  { key: 'type', label: 'Type', text: (c) => TYPE_LABEL[c.claimType] },
  { key: 'reportedDate', label: 'Reported date', text: (c) => dash(c.reportedDate) },
  { key: 'policy', label: 'Policy #', text: (c) => c.policyNumber },
  { key: 'vin', label: 'VIN', text: (c) => dash(c.vehicleVin) },
  { key: 'plate', label: 'Plate', text: (c) => dash(c.vehiclePlate) },
  { key: 'lossState', label: 'Loss state', text: (c) => dash(c.lossState) },
  { key: 'policyState', label: 'Policy state', text: (c) => dash(c.policyState) },
]

export const DEFAULT_COLUMNS: ColumnKey[] = ['claim', 'incidentDate', 'status', 'insured', 'others']

type Body = 'idle' | 'loading' | 'empty' | 'rows'

interface Props {
  columns: ColumnKey[]
  items: ClaimSummary[]
  body: Body
}

// Sorting is done here, in the browser, and only reorders the rows of the current page.
export default function ResultsTable({ columns, items, body }: Props) {
  const [sort, setSort] = useState<{ key: ColumnKey; dir: 'asc' | 'desc' } | null>(null)

  const visible = COLUMNS.filter((c) => columns.includes(c.key))
  const sortColumn = sort && visible.find((c) => c.key === sort.key)

  const rows = useMemo(() => {
    if (!sort || !sortColumn) return items
    const factor = sort.dir === 'asc' ? 1 : -1
    return [...items].sort((a, b) => factor * sortColumn.text(a).localeCompare(sortColumn.text(b)))
  }, [items, sort, sortColumn])

  function toggleSort(key: ColumnKey) {
    setSort((current) => {
      if (current?.key !== key) return { key, dir: 'asc' }
      return current.dir === 'asc' ? { key, dir: 'desc' } : null
    })
  }

  const message =
    body === 'idle' ? { id: 'table-placeholder', title: 'No claims yet', text: 'Run a search to see claims.' }
    : body === 'loading' ? { id: 'loading', title: 'Searching…', text: 'Fetching claims, one moment.' }
    : body === 'empty' ? { id: 'empty-state', title: 'No claims found', text: 'Try a different search or clear the filters.' }
    : null

  return (
    <>
      <div className="results-card">
      <div className="table-scroll">
      <table data-testid="results-table">
        <thead>
          <tr>
            {visible.map((c) => (
              <th key={c.key} data-testid={`header-${c.key}`}
                  aria-sort={sort?.key === c.key ? (sort.dir === 'asc' ? 'ascending' : 'descending') : 'none'}>
                <button type="button" className="sort-button" data-testid={`sort-${c.key}`}
                        onClick={() => toggleSort(c.key)}>
                  {c.label}{sort?.key === c.key ? (sort.dir === 'asc' ? ' ▲' : ' ▼') : ''}
                </button>
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {body === 'rows' && rows.map((claim) => (
            <tr key={claim.id} data-testid="result-row">
              {visible.map((c) => (
                <td key={c.key} data-testid={`result-${c.key}`}>
                  {c.key === 'claim'
                    ? <Link to={`/claims/${claim.id}`} data-testid="result-claim-link">{claim.claimNumber}</Link>
                    : c.text(claim)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
      {message && (
        <div className={`empty-state${body === 'loading' ? ' is-loading' : ''}`} data-testid={message.id}>
          <svg className="empty-logo" viewBox="0 0 48 48" width="56" height="56" aria-hidden="true">
            <path d="M24 4 40 10v12c0 10-7 18-16 22C15 40 8 32 8 22V10z" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinejoin="round" />
            <circle cx="22" cy="22" r="6" fill="none" stroke="currentColor" strokeWidth="2.5" />
            <path d="m26.5 26.5 6 6" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" />
          </svg>
          <strong>{message.title}</strong>
          <span>{message.text}</span>
        </div>
      )}
      </div>
      </div>
    </>
  )
}

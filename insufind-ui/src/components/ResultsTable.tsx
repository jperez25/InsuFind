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
  onColumnsChange: (columns: ColumnKey[]) => void
}

// Sorting is done here, in the browser, and only reorders the rows of the current page.
export default function ResultsTable({ columns, items, body, onColumnsChange }: Props) {
  const [sort, setSort] = useState<{ key: ColumnKey; dir: 'asc' | 'desc' } | null>(null)
  const [pickerOpen, setPickerOpen] = useState(false)

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

  function toggleColumn(key: ColumnKey) {
    const next = columns.includes(key) ? columns.filter((k) => k !== key) : [...columns, key]
    if (next.length > 0) onColumnsChange(COLUMNS.map((c) => c.key).filter((k) => next.includes(k)))
  }

  const message =
    body === 'idle' ? { id: 'table-placeholder', text: 'Run a search to see claims.' }
    : body === 'loading' ? { id: 'loading', text: 'Searching…' }
    : body === 'empty' ? { id: 'empty-state', text: 'No claims found.' }
    : null

  return (
    <>
      <div className="table-tools">
        <button type="button" data-testid="columns-button" aria-expanded={pickerOpen}
                onClick={() => setPickerOpen((open) => !open)}>
          Columns
        </button>
        {pickerOpen && (
          <fieldset className="column-picker" data-testid="column-picker">
            <legend>Show columns</legend>
            {COLUMNS.map((c) => (
              <label key={c.key}>
                <input type="checkbox" data-testid={`column-toggle-${c.key}`} checked={columns.includes(c.key)}
                       onChange={() => toggleColumn(c.key)} />
                {c.label}
              </label>
            ))}
          </fieldset>
        )}
      </div>
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
          {message && (
            <tr>
              <td colSpan={visible.length} data-testid={message.id}>{message.text}</td>
            </tr>
          )}
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
    </>
  )
}

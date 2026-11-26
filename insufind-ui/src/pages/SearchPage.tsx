import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { useSearchParams } from 'react-router-dom'
import { STATUS_LABEL, TYPE_LABEL, searchClaims } from '../api'
import type { ClaimPage, ClaimStatus, ClaimType, SearchCriteria, SearchScope } from '../api'
import ResultsTable, { COLUMNS, DEFAULT_COLUMNS } from '../components/ResultsTable'
import type { ColumnKey } from '../components/ResultsTable'

type Tab = 'driver' | 'vehicle' | 'policy' | 'status'

const TABS: { key: Tab; label: string }[] = [
  { key: 'driver', label: 'Driver' },
  { key: 'vehicle', label: 'Vehicle' },
  { key: 'policy', label: 'Policy' },
  { key: 'status', label: 'Status & type' },
]
const STATUSES = Object.keys(STATUS_LABEL) as ClaimStatus[]
const TYPES = Object.keys(TYPE_LABEL) as ClaimType[]
const PAGE_SIZES = [5, 10, 25, 50]

const list = (value: string | null) => (value ? value.split(',') : [])

// The whole search lives in the URL, so back navigation and reloads restore it. A search has run when `tab` is set.
export default function SearchPage() {
  const [params, setParams] = useSearchParams()
  const searched = params.has('tab')
  const tab = (params.get('tab') ?? 'driver') as Tab
  const page = Number(params.get('page') ?? '0')
  const size = Number(params.get('size') ?? '5')
  const filterStatus = params.get('fstatus') ?? ''
  const filterType = params.get('ftype') ?? ''

  // Local state so the picker reacts instantly; mirrored to the URL (?cols=) so reloads keep it.
  const [columns, setColumns] = useState<ColumnKey[]>(() =>
    params.has('cols')
      ? (list(params.get('cols')).filter((k) => COLUMNS.some((c) => c.key === k)) as ColumnKey[])
      : DEFAULT_COLUMNS)
  const [activeTab, setActiveTab] = useState<Tab>(tab)
  const [draft, setDraft] = useState(() => ({
    q: params.get('q') ?? '',
    scope: (params.get('scope') ?? 'ANY') as SearchScope,
    vin: params.get('vin') ?? '',
    plate: params.get('plate') ?? '',
    policy: params.get('policy') ?? '',
    statuses: list(params.get('statuses')) as ClaimStatus[],
    types: list(params.get('types')) as ClaimType[],
    lossFrom: params.get('lossFrom') ?? '',
    lossTo: params.get('lossTo') ?? '',
    reportedFrom: params.get('reportedFrom') ?? '',
    reportedTo: params.get('reportedTo') ?? '',
  }))
  useEffect(() => setActiveTab(tab), [tab])
  const [result, setResult] = useState<ClaimPage | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(false)

  // The table filters narrow the search server-side and win over the tab's status/type selection.
  const criteria: SearchCriteria = {
    q: params.get('q') ?? undefined,
    scope: (params.get('scope') ?? undefined) as SearchScope | undefined,
    vehicleVin: params.get('vin') ?? undefined,
    vehiclePlate: params.get('plate') ?? undefined,
    policyNumber: params.get('policy') ?? undefined,
    statuses: filterStatus ? [filterStatus as ClaimStatus] : (list(params.get('statuses')) as ClaimStatus[]),
    claimTypes: filterType ? [filterType as ClaimType] : (list(params.get('types')) as ClaimType[]),
    lossDateFrom: params.get('lossFrom') ?? undefined,
    lossDateTo: params.get('lossTo') ?? undefined,
    reportedFrom: params.get('reportedFrom') ?? undefined,
    reportedTo: params.get('reportedTo') ?? undefined,
  }
  const criteriaKey = JSON.stringify(criteria)

  useEffect(() => {
    if (!searched) {
      setResult(null)
      return
    }
    let current = true
    setLoading(true)
    setError(false)
    searchClaims(JSON.parse(criteriaKey), page, size)
      .then((data) => current && setResult(data))
      .catch(() => {
        if (current) {
          setResult(null)
          setError(true)
        }
      })
      .finally(() => current && setLoading(false))
    return () => {
      current = false
    }
  }, [searched, criteriaKey, page, size])

  function update(changes: Record<string, string | null>) {
    const next = new URLSearchParams(params)
    for (const [key, value] of Object.entries(changes)) {
      if (value) next.set(key, value)
      else next.delete(key)
    }
    setParams(next)
  }

  function submit(event: FormEvent) {
    event.preventDefault()
    const onDriver = activeTab === 'driver'
    const onStatus = activeTab === 'status'
    update({
      tab: activeTab,
      q: onDriver ? draft.q.trim() : null,
      scope: onDriver ? draft.scope : null,
      vin: activeTab === 'vehicle' ? draft.vin.trim() : null,
      plate: activeTab === 'vehicle' ? draft.plate.trim() : null,
      policy: activeTab === 'policy' ? draft.policy.trim() : null,
      statuses: onStatus ? draft.statuses.join(',') : null,
      types: onStatus ? draft.types.join(',') : null,
      lossFrom: draft.lossFrom,
      lossTo: draft.lossTo,
      reportedFrom: draft.reportedFrom,
      reportedTo: draft.reportedTo,
      fstatus: null,
      ftype: null,
      page: null,
    })
  }

  function changeColumns(next: ColumnKey[]) {
    setColumns(next)
    const nextParams = new URLSearchParams(params)
    nextParams.set('cols', next.join(','))
    setParams(nextParams, { replace: true })
  }

  function clear() {
    setDraft({ q: '', scope: 'ANY', vin: '', plate: '', policy: '', statuses: [], types: [], lossFrom: '',
      lossTo: '', reportedFrom: '', reportedTo: '' })
    setParams({ cols: columns.join(',') })
  }

  function toggle<T>(values: T[], value: T): T[] {
    return values.includes(value) ? values.filter((v) => v !== value) : [...values, value]
  }

  const field = (name: keyof typeof draft) => ({
    value: draft[name] as string,
    onChange: (e: { target: { value: string } }) => setDraft({ ...draft, [name]: e.target.value }),
  })

  const body = loading ? 'loading' : !result ? 'idle' : result.totalItems === 0 ? 'empty' : 'rows'

  return (
    <section>
      <div role="tablist" className="tabs" aria-label="Search by">
        {TABS.map((t) => (
          <button key={t.key} type="button" role="tab" data-testid={`tab-${t.key}`}
                  aria-selected={activeTab === t.key} onClick={() => setActiveTab(t.key)}>
            {t.label}
          </button>
        ))}
      </div>

      <form onSubmit={submit} className="search-form" role="search">
        {activeTab === 'driver' && (
          <>
            <label>
              Driver name, license or claim number
              <input data-testid="search-input" {...field('q')} placeholder="e.g. Maria Gonzalez or D1000002" />
            </label>
            <label>
              Search in
              <select data-testid="scope-select" value={draft.scope}
                      onChange={(e) => setDraft({ ...draft, scope: e.target.value as SearchScope })}>
                <option value="ANY">Any driver</option>
                <option value="INSURED">Insured driver</option>
                <option value="OTHER">Other drivers involved</option>
              </select>
            </label>
          </>
        )}
        {activeTab === 'vehicle' && (
          <>
            <label>
              VIN
              <input data-testid="vin-input" {...field('vin')} maxLength={17} placeholder="17-character VIN" />
            </label>
            <label>
              License plate
              <input data-testid="plate-input" {...field('plate')} maxLength={15} placeholder="e.g. TST-0001" />
            </label>
          </>
        )}
        {activeTab === 'policy' && (
          <label>
            Policy number
            <input data-testid="policy-input" {...field('policy')} maxLength={30} placeholder="e.g. POL-100001" />
          </label>
        )}
        {activeTab === 'status' && (
          <>
            <fieldset data-testid="status-options">
              <legend>Status</legend>
              {STATUSES.map((s) => (
                <label key={s} className="check">
                  <input type="checkbox" data-testid={`status-option-${s}`} checked={draft.statuses.includes(s)}
                         onChange={() => setDraft({ ...draft, statuses: toggle(draft.statuses, s) })} />
                  {STATUS_LABEL[s]}
                </label>
              ))}
            </fieldset>
            <fieldset data-testid="type-options">
              <legend>Claim type</legend>
              {TYPES.map((t) => (
                <label key={t} className="check">
                  <input type="checkbox" data-testid={`type-option-${t}`} checked={draft.types.includes(t)}
                         onChange={() => setDraft({ ...draft, types: toggle(draft.types, t) })} />
                  {TYPE_LABEL[t]}
                </label>
              ))}
            </fieldset>
          </>
        )}

        <fieldset className="period">
          <legend>Period (optional)</legend>
          <label>Loss from <input type="date" data-testid="loss-from" {...field('lossFrom')} /></label>
          <label>Loss to <input type="date" data-testid="loss-to" {...field('lossTo')} /></label>
          <label>Reported from <input type="date" data-testid="reported-from" {...field('reportedFrom')} /></label>
          <label>Reported to <input type="date" data-testid="reported-to" {...field('reportedTo')} /></label>
        </fieldset>

        <button type="submit" data-testid="search-button">Search</button>
        <button type="button" data-testid="clear-button" onClick={clear}>Clear</button>
      </form>

      {error && <p role="alert" data-testid="error-message">Something went wrong. Please try again.</p>}

      <div className="table-filters" aria-label="Table filters">
        <label>
          Status
          <select data-testid="filter-status" value={filterStatus} disabled={!searched}
                  onChange={(e) => update({ fstatus: e.target.value, page: null })}>
            <option value="">All</option>
            {STATUSES.map((s) => <option key={s} value={s}>{STATUS_LABEL[s]}</option>)}
          </select>
        </label>
        <label>
          Type
          <select data-testid="filter-type" value={filterType} disabled={!searched}
                  onChange={(e) => update({ ftype: e.target.value, page: null })}>
            <option value="">All</option>
            {TYPES.map((t) => <option key={t} value={t}>{TYPE_LABEL[t]}</option>)}
          </select>
        </label>
        <label>
          Rows per page
          <select data-testid="page-size" value={size}
                  onChange={(e) => update({ size: e.target.value, page: null })}>
            {PAGE_SIZES.map((n) => <option key={n} value={n}>{n}</option>)}
          </select>
        </label>
        {result && (
          <p data-testid="results-summary">
            {result.totalItems} {result.totalItems === 1 ? 'claim' : 'claims'}
          </p>
        )}
      </div>

      <ResultsTable columns={columns} items={result?.items ?? []} body={body}
                    onColumnsChange={changeColumns} />

      {result && result.totalItems > 0 && (
        <nav className="pager" aria-label="Pagination">
          <button data-testid="previous-page" disabled={result.page === 0}
                  onClick={() => update({ page: String(result.page - 1) })}>
            Previous
          </button>
          <span data-testid="page-indicator">Page {result.page + 1} of {result.totalPages}</span>
          <button data-testid="next-page" disabled={result.page + 1 >= result.totalPages}
                  onClick={() => update({ page: String(result.page + 1) })}>
            Next
          </button>
        </nav>
      )}
    </section>
  )
}

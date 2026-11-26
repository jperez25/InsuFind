export type ClaimStatus = 'OPEN' | 'IN_REVIEW' | 'APPROVED' | 'DENIED' | 'CLOSED'
export type ClaimType = 'COLLISION' | 'THEFT' | 'GLASS' | 'WEATHER' | 'VANDALISM' | 'OTHER'
export type SearchScope = 'ANY' | 'INSURED' | 'OTHER'

export interface Driver {
  id: number
  fullName: string
  licenseNumber: string
}

export interface ClaimSummary {
  id: number
  claimNumber: string
  incidentDate: string
  status: ClaimStatus
  claimType: ClaimType
  lossState: string | null
  policyNumber: string
  reportedDate: string | null
  vehiclePlate: string | null
  vehicleVin: string | null
  policyState: string | null
  insuredDriver: Driver
  otherDrivers: Driver[]
}

export interface ClaimDetail extends ClaimSummary {
  location: string
  description: string
}

export interface ClaimPage {
  items: ClaimSummary[]
  page: number
  size: number
  totalItems: number
  totalPages: number
}

export const STATUS_LABEL: Record<ClaimStatus, string> = {
  OPEN: 'Open',
  IN_REVIEW: 'In review',
  APPROVED: 'Approved',
  DENIED: 'Denied',
  CLOSED: 'Closed',
}

export const TYPE_LABEL: Record<ClaimType, string> = {
  COLLISION: 'Collision',
  THEFT: 'Theft',
  GLASS: 'Glass',
  WEATHER: 'Weather',
  VANDALISM: 'Vandalism',
  OTHER: 'Other',
}

/** Every field is optional; the ones present are combined with AND (see ClaimSearchCriteria in the API). */
export interface SearchCriteria {
  q?: string
  scope?: SearchScope
  statuses?: ClaimStatus[]
  claimTypes?: ClaimType[]
  lossDateFrom?: string
  lossDateTo?: string
  reportedFrom?: string
  reportedTo?: string
  vehiclePlate?: string
  vehicleVin?: string
  policyNumber?: string
}

export async function searchClaims(criteria: SearchCriteria, page: number, size: number): Promise<ClaimPage> {
  const params = new URLSearchParams({ page: String(page), size: String(size) })
  const response = await fetch(`/api/claims/search?${params}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(criteria),
  })
  if (!response.ok) throw new Error(`Search failed (${response.status})`)
  return response.json()
}

/** Resolves to null when the claim does not exist. */
export async function getClaim(id: string): Promise<ClaimDetail | null> {
  const response = await fetch(`/api/claims/${encodeURIComponent(id)}`)
  if (response.status === 404) return null
  if (!response.ok) throw new Error(`Load failed (${response.status})`)
  return response.json()
}

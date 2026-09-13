import { useEffect, useState } from 'react'

const API_BASE = '/api/employees'

const STATUS_COLORS = {
  VERIFIED: '#1a7f37',
  COMPLETE: '#1a7f37',
  PENDING: '#9a6700',
  NEEDS_HUMAN_REVIEW: '#9a6700',
  REJECTED: '#cf222e',
  INCOMPLETE: '#cf222e',
}

function StatusBadge({ status }) {
  if (!status) return null
  const color = STATUS_COLORS[status] || '#57606a'
  return (
    <span
      style={{
        color,
        border: `1px solid ${color}`,
        borderRadius: '999px',
        padding: '2px 10px',
        fontSize: '0.8rem',
        fontWeight: 600,
      }}
    >
      {status.replaceAll('_', ' ')}
    </span>
  )
}

export default function App() {
  const [employees, setEmployees] = useState([])
  const [form, setForm] = useState({
    name: '',
    hireDate: '',
    documentText: '',
  })
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)

  const loadEmployees = async () => {
    const res = await fetch(API_BASE)
    if (res.ok) {
      setEmployees(await res.json())
    }
  }

  useEffect(() => {
    loadEmployees()
  }, [])

  const handleChange = (field) => (e) => {
    setForm({ ...form, [field]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setErrors({})
    try {
      const res = await fetch(API_BASE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form),
      })
      if (res.status === 400) {
        setErrors(await res.json())
        return
      }
      if (res.ok) {
        setForm({ name: '', hireDate: '', documentText: '' })
        await loadEmployees()
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ maxWidth: 760, margin: '40px auto', fontFamily: 'system-ui, sans-serif', padding: '0 16px' }}>
      <h1 style={{ marginBottom: 4 }}>New Hire Compliance & Document Sync Hub</h1>
      <p style={{ color: '#57606a', marginTop: 0 }}>
        Simulated I-9 Tracker + HRSD-style document intake, with an AI-assisted validation layer
        that a human always reviews before anything downstream trusts it.
      </p>

      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: 12, margin: '24px 0' }}>
        <label>
          Employee name
          <input
            value={form.name}
            onChange={handleChange('name')}
            style={{ width: '100%', padding: 8 }}
            required
          />
          {errors.name && <div style={{ color: '#cf222e', fontSize: '0.85rem' }}>{errors.name}</div>}
        </label>

        <label>
          Hire date
          <input
            type="date"
            value={form.hireDate}
            onChange={handleChange('hireDate')}
            style={{ width: '100%', padding: 8 }}
            required
          />
          {errors.hireDate && <div style={{ color: '#cf222e', fontSize: '0.85rem' }}>{errors.hireDate}</div>}
        </label>

        <label>
          Onboarding document text
          <textarea
            value={form.documentText}
            onChange={handleChange('documentText')}
            rows={4}
            placeholder={'e.g. Name: Jane Doe\\nSSN last 4: 1234\\nDate: 2026-09-01'}
            style={{ width: '100%', padding: 8, fontFamily: 'monospace' }}
            required
          />
          {errors.documentText && <div style={{ color: '#cf222e', fontSize: '0.85rem' }}>{errors.documentText}</div>}
        </label>

        <button type="submit" disabled={loading} style={{ padding: '10px 16px', width: 'fit-content' }}>
          {loading ? 'Submitting…' : 'Onboard employee'}
        </button>
      </form>

      <h2>Employees</h2>
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ textAlign: 'left', borderBottom: '1px solid #d0d7de' }}>
            <th style={{ padding: 8 }}>Name</th>
            <th style={{ padding: 8 }}>Hire date</th>
            <th style={{ padding: 8 }}>I-9 status</th>
            <th style={{ padding: 8 }}>Document status</th>
            <th style={{ padding: 8 }}>Notes</th>
          </tr>
        </thead>
        <tbody>
          {employees.map((emp) => (
            <tr key={emp.id} style={{ borderBottom: '1px solid #eaeef2' }}>
              <td style={{ padding: 8 }}>{emp.name}</td>
              <td style={{ padding: 8 }}>{emp.hireDate}</td>
              <td style={{ padding: 8 }}><StatusBadge status={emp.i9Status} /></td>
              <td style={{ padding: 8 }}><StatusBadge status={emp.documentValidationStatus} /></td>
              <td style={{ padding: 8, color: '#57606a', fontSize: '0.85rem' }}>{emp.documentValidationNotes}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

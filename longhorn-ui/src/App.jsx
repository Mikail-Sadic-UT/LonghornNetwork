import { useState, useEffect } from 'react'
import GraphView from './GraphView'
import RoommateView from './RoommateView'
import ReferralPathView from './ReferralPathView'
import PodView from './PodView'
import SocialView from './SocialView'
import './App.css'

function App() {
  const [data, setData] = useState(null)
  const [activeCase, setActiveCase] = useState(0)

  useEffect(() => {
    fetch('/data.json')
      .then(res => res.json())
      .then(setData)
      .catch(err => console.error('Failed to load data:', err))
  }, [])

  if (!data) return <div className="loading">Loading data...</div>

  const testCase = data[activeCase]

  return (
    <div className="app">
      <div className="topbar">
        <header className="header">
          <h1>Longhorn Network</h1>
          <p className="subtitle">ECE 422C Lab 6 — Student Social Network Visualizer</p>
        </header>

        <nav className="tabs">
          {data.map((tc, i) => (
            <button
              key={i}
              className={`tab ${i === activeCase ? 'active' : ''}`}
              onClick={() => setActiveCase(i)}
            >
              {tc.name}
            </button>
          ))}
        </nav>
      </div>

      {/* Student table — full width */}
      <section className="section">
        <h2>Students ({testCase.students.length})</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Age</th>
                <th>Gender</th>
                <th>Year</th>
                <th>Major</th>
                <th>GPA</th>
                <th>Roommate</th>
                <th>Preferences</th>
                <th>Internships</th>
              </tr>
            </thead>
            <tbody>
              {testCase.students.map(s => (
                <tr key={s.name}>
                  <td className="name-cell">{s.name}</td>
                  <td>{s.age}</td>
                  <td>{s.gender}</td>
                  <td>{s.year}</td>
                  <td>{s.major}</td>
                  <td>{s.gpa}</td>
                  <td>{s.roommate || '—'}</td>
                  <td>{s.roommatePreferences.join(', ') || 'None'}</td>
                  <td>{s.previousInternships.join(', ') || 'None'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      {/* Three-column: Left sidebar | Graph center | Right sidebar */}
      <div className="three-col">
        {/* Left: roommates + pods + referral */}
        <div className="sidebar">
          <section className="section">
            <h2>Roommates</h2>
            <RoommateView students={testCase.students} />
          </section>
          <section className="section">
            <h2>Pods</h2>
            <PodView pods={testCase.pods} />
          </section>
          <section className="section">
            <h2>Referral Path</h2>
            <ReferralPathView referralPaths={testCase.referralPaths} />
          </section>
        </div>

        {/* Center: graph */}
        <section className="section col-graph">
          <h2>Connection Graph</h2>
          <div className="graph-container">
            <GraphView students={testCase.students} edges={testCase.edges} />
          </div>
        </section>

        {/* Right: friends & chat */}
        <div className="sidebar">
          <section className="section">
            <h2>Friends & Chat</h2>
            <SocialView students={testCase.students} />
          </section>
        </div>
      </div>
    </div>
  )
}

export default App

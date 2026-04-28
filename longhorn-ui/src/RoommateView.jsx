// Shows roommate pairs as cards. Unpaired students shown separately.
export default function RoommateView({ students }) {
    // Build pairs (deduplicated) and collect unpaired
    const seen = new Set()
    const pairs = []
    const unpaired = []

    students.forEach(s => {
        if (seen.has(s.name)) return
        if (s.roommate) {
            pairs.push({ a: s.name, b: s.roommate })
            seen.add(s.name)
            seen.add(s.roommate)
        } else {
            unpaired.push(s.name)
            seen.add(s.name)
        }
    })

    return (
        <div className="roommate-grid">
            {pairs.map((p, i) => (
                <div key={i} className="roommate-card paired">
                    <span className="rm-name">{p.a}</span>
                    <span className="rm-icon">+</span>
                    <span className="rm-name">{p.b}</span>
                </div>
            ))}
            {unpaired.map(name => (
                <div key={name} className="roommate-card unpaired">
                    <span className="rm-name">{name}</span>
                    <span className="rm-label">No roommate</span>
                </div>
            ))}
        </div>
    )
}

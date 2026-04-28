// Shows pod groupings as small labeled groups
export default function PodView({ pods }) {
    if (!pods || pods.length === 0) {
        return <p className="muted">No pods formed.</p>
    }

    return (
        <div className="pod-list">
            {pods.map((pod, i) => (
                <div key={i} className="pod-card">
                    <span className="pod-label">Pod {i}</span>
                    <span className="pod-members">{pod.join(', ')}</span>
                </div>
            ))}
        </div>
    )
}

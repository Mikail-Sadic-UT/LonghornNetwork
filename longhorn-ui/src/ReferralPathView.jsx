// Shows multiple referral path queries
export default function ReferralPathView({ referralPaths }) {
    if (!referralPaths || referralPaths.length === 0) {
        return <p className="muted">No referral paths found.</p>
    }

    return (
        <div className="referral-list">
            {referralPaths.map((rp, i) => (
                <div key={i} className="referral-entry">
                    <p className="referral-label">
                        {rp.from} to {rp.company}
                    </p>
                    {rp.path.length === 0 ? (
                        <p className="muted">No path found</p>
                    ) : (
                        <div className="referral-chain">
                            {rp.path.map((name, j) => (
                                <span key={name} className="referral-step">
                                    <span className="referral-node">{name}</span>
                                    {j < rp.path.length - 1 && <span className="referral-arrow">→</span>}
                                </span>
                            ))}
                        </div>
                    )}
                </div>
            ))}
        </div>
    )
}

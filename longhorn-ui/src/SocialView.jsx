// Shows each student's friend list and chat history
export default function SocialView({ students }) {
    return (
        <div className="social-grid">
            {students.map(s => (
                <div key={s.name} className="social-card">
                    <h3 className="social-name">{s.name}</h3>

                    <div className="social-section">
                        <span className="social-label">Friends:</span>
                        <span className="social-value">
                            {s.friends.length > 0 ? s.friends.join(', ') : 'None'}
                        </span>
                    </div>

                    <div className="social-section">
                        <span className="social-label">Chat History:</span>
                        {s.chatHistory.length > 0 ? (
                            <ul className="chat-list">
                                {s.chatHistory.map((msg, i) => (
                                    <li key={i} className="chat-msg">{msg}</li>
                                ))}
                            </ul>
                        ) : (
                            <span className="social-value">None</span>
                        )}
                    </div>
                </div>
            ))}
        </div>
    )
}

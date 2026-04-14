const PollChart = ({ results, totalVotes }) => {
  return (
    <div className="poll-chart">
      {results.map((item) => {
        const percentage = totalVotes > 0 ? Math.round((item.votes / totalVotes) * 100) : 0;
        return (
          <div key={item.candidateId} className="chart-row">
            <div className="chart-label">
              <span>{item.candidateName}</span>
              <span className="vote-count">{item.votes} votes ({percentage}%)</span>
            </div>
            <div className="progress-bar-container">
              <div 
                className="progress-bar-fill" 
                style={{ width: `${percentage}%` }}
              ></div>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default PollChart;
const PollChart = ({ results, totalVotes }) => {
  return (
    <div className="modern-poll-chart">
      {results.map((item) => {
        const percentage = totalVotes > 0 ? Math.round((item.votes / totalVotes) * 100) : 0;
        return (
          <div key={item.candidateId} className="chart-bar-row">
            <div className="chart-bar-info">
              <span className="candidate-name">{item.candidateName}</span>
              <span className="candidate-stats">{item.votes} votes ({percentage}%)</span>
            </div>
            <div className="bar-container">
              <div 
                className="bar-fill" 
                style={{ width: `${percentage}%` }}
              >
                {percentage > 15 && <span className="bar-percentage">{percentage}%</span>}
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default PollChart;
import { Link } from 'react-router-dom';

const ErrorPage = ({ code = 404, message = "Page not found" }) => {
  return (
    <div className="error-page">
      <h1>{code}</h1>
      <p>{message}</p>
      <Link to="/" className="btn-primary">Back to Home</Link>
    </div>
  );
};

export default ErrorPage;
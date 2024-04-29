'use client';
import React, { useState } from 'react';
import Head from 'next/head';
import { useRouter } from 'next/navigation';

const HomePage: React.FC = () => {
  const [name, setName] = useState('');
  const router = useRouter();

  const handleStartGame = () => {
    if (name.trim() !== '') {
      router.push('/game/match?player='+name);
    } else {
      alert('Please enter your name before starting the game.');
    }
  };

  const handleCheckPlayerRating = () => {
    if (name.trim() !== '') {
      alert('Rating is ');
    } else {
      alert('Please enter the player name to check their rating.');
    }
  };

  return (
    <div className="container">
      <Head>
        <title>Welcome to Click2Win</title>
      </Head>

      <main>
        <h1 className="title">Welcome to Click2Win!</h1>

        <div className="input-container">
          <label htmlFor="nameInput">Player Name:</label>
          <input
            id="nameInput"
            type="text"
            maxLength={40}
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        <p className="description">
          <button className="btn" onClick={handleStartGame}>
            Start the match
          </button>
        </p>

        <p className="description">
          <button className="btn" onClick={handleCheckPlayerRating}>
            Show The Player Rating
          </button>
        </p>

        <div className="leaderboard-container">
          <h2 className="leaderboard-title">Top 3 Players</h2>
          <table className="leaderboard">
            <thead>
              <tr>
                <th>Name</th>
                <th>Rating</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Player 1</td>
                <td>1200</td>
              </tr>
              <tr>
                <td>Player 2</td>
                <td>1100</td>
              </tr>
              <tr>
                <td>Player 3</td>
                <td>1050</td>
              </tr>
            </tbody>
          </table>
        </div>
      </main>

      <style jsx>{`
        .container {
          min-height: 100vh;
          padding: 0 1rem;
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
        }

        main {
          padding: 5rem 0;
          display: flex;
          flex-direction: column;
          align-items: center;
        }

        .title {
          margin-bottom: 2rem;
          line-height: 1.15;
          font-size: 3rem;
          text-align: center;
        }

        .input-container {
          margin-bottom: 1.5rem;
          display: flex;
          align-items: center;
        }

        label {
          margin-right: 1rem;
        }

        .btn {
          padding: 1rem 2rem;
          border-radius: 5px;
          background-color: #0070f3;
          color: #fff;
          text-decoration: none;
          font-size: 1.2rem;
          cursor: pointer;
        }

        .leaderboard-container {
          margin-top: 3rem;
        }

        .leaderboard-title {
          font-size: 2rem;
          margin-bottom: 1rem;
        }

        .leaderboard {
          width: 80%;
          border-collapse: collapse;
        }

        th,
        td {
          border: 1px solid #ddd;
          padding: 10px;
          text-align: left;
        }

        th {
          background-color: #f2f2f2;
        }
      `}</style>
    </div>
  );
};

export default HomePage;

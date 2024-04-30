'use client';
import React, { useEffect, useState } from 'react';
import Head from 'next/head';
import { useRouter } from 'next/navigation';
import { apiUrl } from './api/constants';

type Player = {
  userId: number;
  playerName: string;
  rating: number;
}

const HomePage: React.FC = () => {
  const [name, setName] = useState('');
  const [apiUrl, SetUrl] = useState<string>('');
  const [error, setError] = useState<boolean>(false);
  const [players, setPlayers] = useState<Player[]>([]);
  const [topPlayersCount, setTopPlayersCount] = useState<number | null>(null);
  const [playerRating, setPlayerRating] = useState<number>(0);
  const router = useRouter();

  const handleStartGame = () => {
    if (name.trim() !== '') {
      router.push('/game/match?player=' + name);
    } else {
      alert('Please enter your name before starting the game.');
    }
  };

  const handleCheckPlayerRating = async () => {
    if (name.trim() !== '') {
      try {
        const response = await fetch(apiUrl + '/rating?playerName=' + name);
        if (!response.ok) {
          throw new Error('Failed to fetch player rating');
        }
        const rating = await response.json();
        setPlayerRating(rating);
        alert('Rating of ' + name + ' is ' + rating);
      } catch (error) {
        console.error('Error fetching player rating:', error);
        setError(true);
      }
    } else {
      alert('Please enter the player name to check their rating.');
    }
  };

  useEffect(() => {
    const queryString = window.location.search;
    const queryParams = new URLSearchParams(queryString);
    const playerName = queryParams.get('player')?? "default";
    console.log("player name is " + playerName)
    const env = queryParams.get('env')?? "prod";
    var apiUrl: string;
    if(env === "prod") {
      apiUrl ='https://4609d7e8-cc66-4077-9781-04910e97ccb3-prod.e1-us-cdp-2.choreoapis.dev/dxxo/game-server-http/player-click-5c6/v1.0/api/player'
    } else if (env === "dev"){
      apiUrl ='https://4609d7e8-cc66-4077-9781-04910e97ccb3-dev.e1-us-cdp-2.choreoapis.dev/dxxo/game-server-http/start-new-game-session-5c6/v1.0/api/player';
    } else {
      apiUrl = 'http://localhost:8080/api/player';
    }
    SetUrl(apiUrl)

    const fetchPlayerCount = async () => {
      try {
        const response = await fetch(apiUrl + '/count');
        if (!response.ok) {
          throw new Error('Failed to fetch player count');
        }
        const count = await response.json();
        setTopPlayersCount(count);
      } catch (error) {
        console.error('Error fetching player count:', error);
        setError(true);
      }
    };

    const fetchTopPlayers = async () => {
      try {
        const response = await fetch(apiUrl + '/top');
        if (!response.ok) {
          throw new Error('Failed to fetch player data');
        }
        const data = await response.json();
        setPlayers(data);
      } catch (error) {
        console.error('Error fetching players:', error);
        setError(true);
      }
    };

    fetchPlayerCount();
    fetchTopPlayers();
  }, []);

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
          <button className="btn" onClick={handleCheckPlayerRating}>
            Show The Player Rating
          </button>
        </p>

        <div className="leaderboard-container">
          {topPlayersCount !== null && (
            <h2 className="leaderboard-title">Top {topPlayersCount} Players</h2>
          )}
          {error ? (
            <p>Database is on maintenance. Top Players information is not available.</p>
          ) : (
            <table className="leaderboard">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Rating</th>
                </tr>
              </thead>
              <tbody>
                {players.slice(0, topPlayersCount || 3).map((player) => (
                  <tr key={player.userId}>
                    <td>{player.playerName}</td>
                    <td>{player.rating}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
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
          margin-right: 1rem;
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

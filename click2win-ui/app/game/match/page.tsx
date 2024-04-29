"use client";
import React, { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';
 
type PlayerClickedEvent = {
  x: number;
  y: number;
};

type ServerGameEvent = {
  x: number;
  y: number;
  end: boolean;
  score: number;
};

const Page: React.FC = () => {
  const apiUrl ='https://4609d7e8-cc66-4077-9781-04910e97ccb3-dev.e1-us-cdp-2.choreoapis.dev/dxxo/game-server-http/start-new-game-session-5c6/v1.0/api/gamesession'
  // const apiUrl = 'http://localhost:8080/api/gamesession';

  const router = useRouter(); 
  const [resp, setResp] = useState<ServerGameEvent | null>(null);
  const [playerName, setPlayerName] = useState<string>('Default');
  const [session, setSession] = useState<string>('');
  const [timer, setTimer] = useState(60);
  const [score, setScore] = useState(0);
  const [gameOver, setGameOver] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    const countdown = setInterval(() => {
      setTimer((prevTimer) => {
        if (prevTimer > 0) {
          return prevTimer - 1;
        } else {
          setGameOver(true);
          return 0;
        }
      });
    }, 1000);
    return () => clearInterval(countdown);
  }, []);

  // useEffect(function mount() {
  //   console.log('window.configs=' + window?.configs?.apiUrl ? window.configs.apiUrl : "/");
  // });

  useEffect(() => {
    const queryString = window.location.search;
    const queryParams = new URLSearchParams(queryString);
    const playerName = queryParams.get('player')?? "default";
    console.log("player name is " + playerName)
    setPlayerName(playerName);
    const fetchSession = async () => {
      let sessionId = 'empty';
      try {
        const response = await fetch(apiUrl+"/start?player="+playerName, {
          method: 'POST'
        });
        sessionId = await response.text();
        console.log('session from server: ' + sessionId)
        setSession(sessionId);
      } catch (error) {
        console.error('Error starting new session:', error);
        setGameOver(true);
        setErrorMessage('Match error');
      }
      return sessionId;
    };
  
    const fetchEvents = async (session: string) => {
    try {
      console.log("call "+ apiUrl+"/next?session="+session);
      const response = await fetch(apiUrl+"/next?session="+session);
      const randomEvent: ServerGameEvent = await response.json(); // Get the response as an ArrayBuffer
      console.log('ServerGameEvent received: ', randomEvent);
      console.log(`ServerGameEvent x: ${randomEvent.x}, y: ${randomEvent.y}, score: ${randomEvent.score}, end: ${randomEvent.end}`);
      if (randomEvent.score) {
        setScore(randomEvent.score);
      }
      setResp(randomEvent);
    } catch (error) {
      console.error('Error fetching events:', error);
      setGameOver(true);
      setErrorMessage('Match error');
    }
  };
  
  const runAsyncTasks = async () => {
    const sessionId = await fetchSession();
    // fetchEvents(sessionId);
    const interval = setInterval(() => fetchEvents(sessionId), 1000);
    return () => clearInterval(interval);
  };
  
    runAsyncTasks();
  }, []); // Empty dependency array to run effect only once


  const handleClick = async (x: number, y: number) => {
    try {
      const pl: PlayerClickedEvent = { x, y };
      const response = await fetch(apiUrl + "/click?session="+session, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(pl)
      });

      if (!response.ok) {
        throw new Error('Failed to send click event');
      }
    } catch (error) {
      console.error('Error sending click event:', error);
      setGameOver(true);
      setErrorMessage('Match error');
    }
  };

  const handleLeaveMatch = () => {
    router.push('/');
  };
  
  return (
    <div style={{ position: 'relative', textAlign: 'center', fontFamily: 'Arial, sans-serif', minHeight: '100vh', paddingTop: '20px' }}>
    <div style={{ marginBottom: '20px' }}>
      <p style={{ fontSize: '20px', fontWeight: 'bold' }}>Timer: {timer}</p>
      <p style={{ fontSize: '20px', fontWeight: 'bold' }}>Score: {score}</p>
    </div>
    {gameOver && (
      <div style={{ textAlign: 'center', marginBottom: '20px' }}>
        <h1 style={{ fontSize: '24px', color: 'red' }}>{errorMessage || 'Match completed'}</h1>
        <p style={{ fontSize: '18px' }}>Final Score: {score}</p>
      </div>
    )}
    <div 
      id="gameField"
      style={{
        position: 'relative',
        width: '500px',
        height: '500px',
        margin: '0 auto',
        border: '1px solid black',
        backgroundColor: '#f0f0f0',
        marginBottom: '20px'
      }}
    >
      {resp && (
        <button 
          id="targetButton"
          style={{
            position: 'absolute',
            left: `${resp.x}px`,
            top: `${resp.y}px`
          }}
          onClick={() => handleClick(resp.x, resp.y)}
        >
          CLICK ME
        </button>
      )}
    </div>
    <p style={{ fontSize: '20px', marginBottom: '20px' }}>
      Playername: {playerName}
    </p>
    <button 
      style={{
        backgroundColor: gameOver ? 'green' : 'red',
        color: 'white',
        fontSize: '16px',
        padding: '10px 20px',
        borderRadius: '5px',
        cursor: 'pointer'
      }}
      onClick={handleLeaveMatch}
    >
      {gameOver ? 'Return to main page' : 'Leave match'}
    </button>
  </div>
  );
};

export default Page;
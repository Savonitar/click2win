"use client";
import { ServerGameEvent, PlayerClickedEvent } from '@/app/proto/api_pb';
import { Message, proto3 } from "@bufbuild/protobuf";
import React, { useState, useEffect, useRef } from 'react';
import { apiUrl } from "../../api/constants";

const Page: React.FC = () => {
  const [resp, setResp] = useState<ServerGameEvent | null>(null);
  const [socket, setSocket] = useState<WebSocket | null>(null);
  const [timer, setTimer] = useState(5);
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


  useEffect(() => {
    // const socket = new WebSocket('ws://localhost:8080/streaming');
    // http://game-session-server-491256193:8080/gamesession
    // https://4609d7e8-cc66-4077-9781-04910e97ccb3-dev.e1-us-cdp-2.choreoapis.dev/dxxo/game-session-server/game-session-service-cdc/v1.2
    // const socket = new WebSocket('wss://4609d7e8-cc66-4077-9781-04910e97ccb3-dev.e1-us-cdp-2.choreoapis.dev/dxxo/game-session-server/game-session-service-cdc/v1.2');
    console.log('Trying to create a websocket')
    console.log('Trying to create a websocket for url `${apiUrl}`')
    console.log('Trying to create a websocket for url =' + apiUrl)
    const socket = new WebSocket(apiUrl);
    setSocket(socket);
    socket.addEventListener('open', () => {
      console.log('WebSocket connection established');
      // socket.send('Hello, server!');
    });
    socket.addEventListener('message', (event) => {
          console.log('Response from server:', event.data);
          event.data.arrayBuffer().then((arrayBuffer: ArrayBuffer) => {
          const uint8Array = new Uint8Array(arrayBuffer);
          const randomEvent = ServerGameEvent.fromBinary(uint8Array);
          console.log('ServerGameEvent received: ', randomEvent);
          if (randomEvent.score) {
            setScore(randomEvent.score);
          }
          setResp(randomEvent);
      });
    });

    socket.addEventListener('error', (event) => {
      console.error('WebSocket error:', event);
      setGameOver(true);
      setErrorMessage('Match error');
    });

    socket.addEventListener('close', (event) => {
      console.log('WebSocket connection closed: ', event);
      setGameOver(true);
    });

    return () => {
      socket.close();
    };
  }, []); // Empty dependency array to run the effect only once
  const handleClick = useRef<(x: number, y: number) => void>(() => {});

useEffect(() => {
  handleClick.current = (x: number, y: number) => {
    if (socket) {
      var pl = new PlayerClickedEvent();
      pl.x = x;
      pl.y = y;
      const binaryData = (pl as Message<PlayerClickedEvent>).toBinary();
      socket.send(binaryData);
    }
  };
}, [socket]);

useEffect(() => {
  const button = document.getElementById('targetButton');
  if (button && resp) {
    button.addEventListener('click', () => handleClick.current(resp.targetX, resp.targetY));
    return () => {
      button.removeEventListener('click', () => handleClick.current);
    };
  }
}, [resp]);
  
  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', position: 'absolute', top: '10%', left: '50%', transform: 'translate(-50%, -100%)' }}>
        <p>Timer: {timer}    </p>
        <p>Score: {score}</p>
      </div>
      {gameOver && (
        <div style={{ position: 'absolute', top: '20%', left: '50%', transform: 'translate(-50%, -50%)', textAlign: 'center' }}>
          <h1>{errorMessage || 'Match completed'}</h1>
          <p>Score: {score}</p>
        </div>
      )}
      <div 
        id="gameField"
        style={{
          position: 'absolute',
          left: '50%',
          top: '50%',
          transform: 'translate(-50%, -50%)',
          width: '500px',
          height: '500px',
          border: '1px solid black',
          backgroundColor: '#f0f0f0'
        }}
      >
        {resp && (
          <button 
            id="targetButton"
            style={{
              position: 'absolute',
              left: `${resp.targetX}px`,
              top: `${resp.targetY}px`
            }}
          >
            CLICK ME
          </button>
        )}
      </div>
      <button style={{ position: 'absolute', top: '90%', left: '50%', transform: 'translate(-50%, -50%)', backgroundColor: gameOver ? 'green' : 'red' }}>
        {gameOver ? 'Return to main page' : 'Leave match'}
      </button>
    </div>
  );
};

export default Page;
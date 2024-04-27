"use client";
import React, { useState, useEffect } from 'react';

const Page: React.FC = () => {
  const [resp, setResp] = useState<string | null>(null);

  useEffect(() => {
    const socket = new WebSocket('ws://localhost:8080/leader');

    socket.addEventListener('open', () => {
      console.log('WebSocket connection established');
    });

    socket.addEventListener('message', (event) => {
      console.log('Message from server:', event.data);
      setResp(event.data);
    });

    socket.addEventListener('error', (event) => {
      console.error('WebSocket error:', event);
    });

    socket.addEventListener('close', () => {
      console.log('WebSocket connection closed');
    });

    return () => {
      socket.close();
    };
  }, []); // Empty dependency array to run the effect only once

  return (
    <p>Leader Page r={resp}</p>
  );
};

export default Page;
import React, { useState, useEffect } from 'react';
import { Server, Activity } from 'lucide-react';
import { checkServicesHealth } from '../services/notificationApi';

export default function ServiceStatusIndicator() {
  const [status, setStatus] = useState({
    taskService: true,
    notificationService: true,
  });

  const checkHealth = async () => {
    const res = await checkServicesHealth();
    setStatus(res);
  };

  useEffect(() => {
    checkHealth();
    const interval = setInterval(checkHealth, 8000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="service-status-cluster" title="Arquitetura Distribuída de Microsserviços">
      <div className={`service-pill ${status.taskService ? 'online' : 'offline'}`}>
        <Server size={13} />
        <span className="service-name">Task Service</span>
        <span className="service-port">:8080</span>
        <span className="status-dot"></span>
      </div>

      <div className={`service-pill ${status.notificationService ? 'online' : 'offline'}`}>
        <Activity size={13} />
        <span className="service-name">Notification Service</span>
        <span className="service-port">:8082</span>
        <span className="status-dot"></span>
      </div>
    </div>
  );
}

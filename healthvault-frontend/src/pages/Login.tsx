import React, { useState } from 'react';
import { Box, Card, Typography, TextField, Button, InputAdornment, IconButton } from '@mui/material';
import { Mail, Lock, HeartPulse, ArrowRight } from 'lucide-react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    // Temporary bypass for UI demonstration
    navigate('/dashboard');
  };

  return (
    <Box sx={{ 
      minHeight: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #f8fafc 0%, #e0e7ff 100%)'
    }}>
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, ease: "easeOut" }}
      >
        <Card sx={{ 
          p: 5, 
          width: { xs: '90vw', sm: 450 }, 
          borderRadius: 4,
          boxShadow: '0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1)',
          background: 'rgba(255, 255, 255, 0.9)',
          backdropFilter: 'blur(10px)'
        }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 4, color: 'primary.main' }}>
            <HeartPulse size={36} strokeWidth={2.5} />
            <Typography variant="h4" fontWeight={800} letterSpacing="-1px">
              HealthVault
            </Typography>
          </Box>

          <Typography variant="h5" fontWeight={700} color="text.primary" mb={1}>
            Welcome back
          </Typography>
          <Typography variant="body2" color="text.secondary" mb={4}>
            Enter your credentials to access your medical records.
          </Typography>

          <form onSubmit={handleLogin}>
            <TextField
              fullWidth
              label="Email Address"
              variant="outlined"
              margin="normal"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Mail size={20} color="#94a3b8" />
                  </InputAdornment>
                ),
              }}
              sx={{ mb: 2 }}
            />
            
            <TextField
              fullWidth
              label="Password"
              type="password"
              variant="outlined"
              margin="normal"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Lock size={20} color="#94a3b8" />
                  </InputAdornment>
                ),
              }}
              sx={{ mb: 4 }}
            />

            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              endIcon={<ArrowRight size={20} />}
              sx={{ py: 1.5, fontSize: '1.1rem' }}
            >
              Sign In
            </Button>
          </form>

          <Box sx={{ mt: 4, textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              Don't have an account? <span style={{ color: '#2563eb', cursor: 'pointer', fontWeight: 600 }}>Create one</span>
            </Typography>
          </Box>
        </Card>
      </motion.div>
    </Box>
  );
};

export default Login;

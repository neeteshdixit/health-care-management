import React from 'react';
import { Box, AppBar, Toolbar, Typography, Avatar, Container, Grid, Card, CardContent, Button, IconButton } from '@mui/material';
import { HeartPulse, Bell, Search, FileText, Activity, Pill, Plus } from 'lucide-react';
import { motion } from 'framer-motion';

const PatientDashboard = () => {
  return (
    <Box sx={{ flexGrow: 1, minHeight: '100vh', bgcolor: 'background.default' }}>
      {/* App Bar */}
      <AppBar position="static" color="inherit" elevation={0} sx={{ borderBottom: '1px solid #e2e8f0' }}>
        <Toolbar sx={{ justifyContent: 'space-between' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: 'primary.main' }}>
            <HeartPulse size={28} strokeWidth={2.5} />
            <Typography variant="h6" fontWeight={700} letterSpacing="-0.5px">
              HealthVault
            </Typography>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <IconButton color="primary">
              <Search size={20} />
            </IconButton>
            <IconButton color="primary">
              <Bell size={20} />
            </IconButton>
            <Avatar sx={{ bgcolor: 'primary.main', width: 35, height: 35 }}>N</Avatar>
          </Box>
        </Toolbar>
      </AppBar>

      {/* Main Content */}
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
            <Typography variant="h4" fontWeight={700}>
              Overview
            </Typography>
            <Button variant="contained" startIcon={<Plus size={18} />} sx={{ borderRadius: 8 }}>
              Upload Record
            </Button>
          </Box>

          <Grid container spacing={3}>
            {/* Summary Cards */}
            <Grid item xs={12} md={4}>
              <Card sx={{ borderRadius: 3 }}>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Box sx={{ p: 1.5, borderRadius: 2, bgcolor: '#eff6ff', color: '#3b82f6' }}>
                      <FileText size={24} />
                    </Box>
                    <Typography variant="h4" fontWeight={700}>12</Typography>
                  </Box>
                  <Typography variant="body1" color="text.secondary" fontWeight={600}>Total Records</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} md={4}>
              <Card sx={{ borderRadius: 3 }}>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Box sx={{ p: 1.5, borderRadius: 2, bgcolor: '#f0fdf4', color: '#22c55e' }}>
                      <Activity size={24} />
                    </Box>
                    <Typography variant="h4" fontWeight={700}>3</Typography>
                  </Box>
                  <Typography variant="body1" color="text.secondary" fontWeight={600}>Active Doctors</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} md={4}>
              <Card sx={{ borderRadius: 3 }}>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Box sx={{ p: 1.5, borderRadius: 2, bgcolor: '#fef2f2', color: '#ef4444' }}>
                      <Pill size={24} />
                    </Box>
                    <Typography variant="h4" fontWeight={700}>2</Typography>
                  </Box>
                  <Typography variant="body1" color="text.secondary" fontWeight={600}>Active Prescriptions</Typography>
                </CardContent>
              </Card>
            </Grid>

            {/* Timeline Placeholder */}
            <Grid item xs={12}>
              <Card sx={{ borderRadius: 3, minHeight: 300, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Box sx={{ textAlign: 'center', color: 'text.secondary' }}>
                  <Activity size={48} opacity={0.3} style={{ marginBottom: 16 }} />
                  <Typography variant="h6">Medical Timeline</Typography>
                  <Typography variant="body2">Your chronological history will appear here once you upload records.</Typography>
                </Box>
              </Card>
            </Grid>
          </Grid>
        </motion.div>
      </Container>
    </Box>
  );
};

export default PatientDashboard;

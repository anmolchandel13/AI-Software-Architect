import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { projectService } from '../services/projectService';
import { Terminal, Send, HelpCircle, ArrowLeft, Lightbulb } from 'lucide-react';
import Loader from '../components/common/Loader';

/**
 * Generation page where users enter ideas to let the AI generate architecture.
 */
const GeneratePage = () => {
  const navigate = useNavigate();
  const [idea, setIdea] = useState('');
  const [loading, setLoading] = useState(false);
  const [statusMessage, setStatusMessage] = useState('Initiating Generation...');
  const [error, setError] = useState('');

  const suggestions = [
    "Build a Hospital Management System with Doctor appointments, Billing, and Medical records",
    "Design a Food Delivery App Backend with Real-time order tracking, Driver routing, and Payments",
    "Create a Premium E-commerce Platform with Multi-vendor support, Product search, and Stripe payment gateway"
  ];

  // Rotate status message during generation to keep user engaged
  useEffect(() => {
    let interval;
    if (loading) {
      const messages = [
        "Analyzing project scope and system requirements...",
        "Formulating structured architecture context prompts...",
        "Connecting to Google Gemini AI API...",
        "Generating system overview and module designs...",
        "Drafting complete REST API endpoints schema...",
        "Writing Hibernate entities and Spring Boot folder tree...",
        "Compiling database schemas and ER diagram data...",
        "Parsing final JSON report configuration...",
        "Saving project blueprints to database..."
      ];
      let i = 0;
      interval = setInterval(() => {
        setStatusMessage(messages[i % messages.length]);
        i++;
      }, 3000);
    } else {
      setStatusMessage('Initiating Generation...');
    }
    return () => clearInterval(interval);
  }, [loading]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (idea.trim().length < 10) {
      setError('Project idea must be at least 10 characters.');
      return;
    }

    try {
      setError('');
      setLoading(true);
      const data = await projectService.generateProject(idea);
      navigate(`/projects/${data.id}`);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to generate project. Make sure your API Key is valid.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ minHeight: '80vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <div className="glass-panel" style={{ width: '100%', maxWidth: '500px', padding: '40px', textAlign: 'center', borderRadius: '24px' }}>
          <Loader message={statusMessage} />
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '10px' }}>
            This can take up to 15 seconds. Please do not close or refresh this page.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '0 20px 40px' }}>
      
      {/* Back Button */}
      <button 
        onClick={() => navigate('/')} 
        style={{ display: 'flex', alignItems: 'center', gap: '6px', background: 'transparent', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', margin: '30px 0 20px', fontSize: '0.9rem', transition: 'var(--transition)' }}
        onMouseEnter={e => e.currentTarget.style.color = 'var(--text-primary)'}
        onMouseLeave={e => e.currentTarget.style.color = 'var(--text-secondary)'}
      >
        <ArrowLeft size={16} />
        <span>Back to Dashboard</span>
      </button>

      {/* Main Container */}
      <div className="glass-panel" style={{ padding: '40px', borderRadius: '24px' }}>
        <div style={{ display: 'flex', gap: '15px', alignItems: 'flex-start', marginBottom: '30px' }}>
          <div style={{ background: 'var(--primary-gradient)', padding: '12px', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Terminal size={24} color="white" />
          </div>
          <div>
            <h1 style={{ fontSize: '1.75rem', fontWeight: '800', marginBottom: '6px' }}>Generate Architecture</h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
              Explain your application concept in plain English and let the AI compile complete plans.
            </p>
          </div>
        </div>

        {error && (
          <div style={{ padding: '12px', background: 'rgba(239, 68, 68, 0.1)', border: '1px solid var(--error)', borderRadius: '8px', color: 'var(--error)', fontSize: '0.85rem', marginBottom: '20px' }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '8px', fontWeight: '500' }}>
              Explain Your Project Idea
            </label>
            <textarea 
              className="form-input" 
              style={{ minHeight: '180px', fontFamily: 'inherit', resize: 'vertical', lineHeight: '1.6' }} 
              placeholder="e.g. Build a Food Delivery backend system. Needs drivers, restaurant manager interfaces, customer apps, and real-time tracking with Google Maps..."
              value={idea}
              onChange={e => setIdea(e.target.value)}
              required
            />
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '6px' }}>
              <span>Explain requirements, modules, and target users for best results.</span>
              <span>{idea.length} / 2000 characters</span>
            </div>
          </div>

          <button type="submit" className="btn-primary" style={{ width: 'fit-content', alignSelf: 'flex-end' }}>
            <span>Generate System Blueprint</span>
            <Send size={16} />
          </button>
        </form>

        {/* Suggestions Section */}
        <div style={{ borderTop: '1px solid var(--border-color)', marginTop: '40px', paddingTop: '30px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '15px', color: 'var(--primary)', fontWeight: '600', fontSize: '0.95rem' }}>
            <Lightbulb size={18} />
            <span>Need inspiration? Try these templates:</span>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            {suggestions.map((s, idx) => (
              <div 
                key={idx} 
                onClick={() => setIdea(s)}
                style={{ 
                  padding: '12px 16px', 
                  background: 'rgba(255,255,255,0.02)', 
                  border: '1px solid var(--border-color)', 
                  borderRadius: '10px', 
                  cursor: 'pointer',
                  fontSize: '0.85rem',
                  color: 'var(--text-secondary)',
                  transition: 'var(--transition)'
                }}
                onMouseEnter={e => { e.currentTarget.style.borderColor = 'var(--primary)'; e.currentTarget.style.color = 'var(--text-primary)'; }}
                onMouseLeave={e => { e.currentTarget.style.borderColor = 'var(--border-color)'; e.currentTarget.style.color = 'var(--text-secondary)'; }}
              >
                {s}
              </div>
            ))}
          </div>
        </div>

      </div>
    </div>
  );
};

export default GeneratePage;

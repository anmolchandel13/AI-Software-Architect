import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { projectService } from '../services/projectService';
import { Search, Plus, Trash2, Calendar, FileText, ChevronRight, AlertCircle } from 'lucide-react';
import Loader from '../components/common/Loader';

/**
 * Main workspace dashboard. Displays project history, keyword searching,
 * and project deletions.
 */
const DashboardPage = () => {
  const navigate = useNavigate();
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    try {
      setLoading(true);
      const data = await projectService.getAllProjects();
      setProjects(data);
    } catch (err) {
      setError('Could not fetch project history.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchQuery.trim()) {
      fetchProjects();
      return;
    }

    try {
      setLoading(true);
      const data = await projectService.searchProjects(searchQuery);
      setProjects(data);
    } catch (err) {
      setError('Failed to search projects.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id, title, e) => {
    e.stopPropagation();
    e.preventDefault();
    if (!window.confirm(`Are you sure you want to delete the project: "${title}"?`)) {
      return;
    }

    try {
      await projectService.deleteProject(id);
      setProjects(projects.filter(p => p.id !== id));
    } catch (err) {
      alert('Failed to delete project.');
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  const getStatusStyle = (status) => {
    switch (status) {
      case 'COMPLETED':
        return { color: 'var(--success)', background: 'rgba(16, 185, 129, 0.1)' };
      case 'PROCESSING':
      case 'PENDING':
        return { color: 'var(--warning)', background: 'rgba(245, 158, 11, 0.1)' };
      default:
        return { color: 'var(--error)', background: 'rgba(239, 68, 68, 0.1)' };
    }
  };

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '0 20px 40px' }}>
      
      {/* Header Section */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', margin: '40px 0 30px' }}>
        <div>
          <h1 style={{ fontSize: '2rem', fontWeight: '800', marginBottom: '8px' }}>Architect Workspace</h1>
          <span style={{ color: 'var(--text-secondary)' }}>Manage your generated systems and deploy blueprints</span>
        </div>
        <Link to="/generate" className="btn-primary" style={{ textDecoration: 'none' }}>
          <Plus size={20} />
          <span>New Architecture</span>
        </Link>
      </div>

      {/* Search Bar */}
      <form onSubmit={handleSearch} style={{ display: 'flex', gap: '10px', marginBottom: '30px' }}>
        <div style={{ position: 'relative', flex: 1 }}>
          <Search size={18} color="var(--text-muted)" style={{ position: 'absolute', left: '16px', top: '15px' }} />
          <input 
            type="text" 
            className="form-input" 
            style={{ paddingLeft: '50px' }} 
            placeholder="Search architectures by keyword..." 
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
          />
        </div>
        <button type="submit" className="btn-primary" style={{ padding: '0 24px', borderRadius: '12px' }}>
          Search
        </button>
      </form>

      {error && (
        <div style={{ padding: '15px', background: 'rgba(239, 68, 68, 0.1)', border: '1px solid var(--error)', borderRadius: '12px', color: 'var(--error)', display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '30px' }}>
          <AlertCircle size={20} />
          <span>{error}</span>
        </div>
      )}

      {/* Projects Grid/List */}
      {loading ? (
        <Loader message="Fetching projects list..." />
      ) : projects.length === 0 ? (
        <div className="glass-panel" style={{ padding: '60px 20px', textAlign: 'center', borderRadius: '24px' }}>
          <FileText size={48} color="var(--text-muted)" style={{ marginBottom: '15px' }} />
          <h3 style={{ fontSize: '1.25rem', fontWeight: '600', marginBottom: '8px' }}>No Architectures Found</h3>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '20px', fontSize: '0.95rem' }}>
            Type your first project concept to let the AI architect design a system blueprint.
          </p>
          <Link to="/generate" className="btn-primary" style={{ textDecoration: 'none' }}>
            <Plus size={18} />
            <span>Generate Now</span>
          </Link>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(360px, 1fr))', gap: '20px' }}>
          {projects.map((project) => (
            <div 
              key={project.id} 
              className="glass-panel" 
              onClick={() => navigate(`/projects/${project.id}`)}
              style={{ padding: '25px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', borderRadius: '20px', cursor: 'pointer' }}
            >
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '10px', marginBottom: '15px' }}>
                  <span 
                    style={{ 
                      padding: '4px 10px', 
                      borderRadius: '6px', 
                      fontSize: '0.75rem', 
                      fontWeight: '700',
                      ...getStatusStyle(project.status)
                    }}
                  >
                    {project.status}
                  </span>
                  <button 
                    onClick={(e) => handleDelete(project.id, project.title, e)} 
                    style={{ background: 'transparent', border: 'none', cursor: 'pointer', padding: '4px', borderRadius: '6px', transition: 'var(--transition)' }}
                    onMouseEnter={e => e.currentTarget.style.background = 'rgba(239, 68, 68, 0.1)'}
                    onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
                  >
                    <Trash2 size={16} color="var(--text-muted)" onMouseEnter={e => e.target.setAttribute('color', 'red')} onMouseLeave={e => e.target.setAttribute('color', 'gray')} />
                  </button>
                </div>

                <h3 style={{ fontSize: '1.2rem', fontWeight: '700', marginBottom: '10px', textOverflow: 'ellipsis', overflow: 'hidden', whiteSpace: 'nowrap' }}>
                  {project.title}
                </h3>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', marginBottom: '20px', display: '-webkit-box', WebKitLineBreak: 'auto', overflow: 'hidden', height: '36px', WebKitLineClamp: 2, WebKitBoxOrient: 'vertical' }}>
                  {project.originalIdea}
                </p>
              </div>

              <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '15px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', fontSize: '0.8rem' }}>
                  <Calendar size={14} />
                  <span>{formatDate(project.createdAt)}</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: 'var(--primary)', fontSize: '0.85rem', fontWeight: '600' }}>
                  <span>View Details</span>
                  <ChevronRight size={16} />
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default DashboardPage;
